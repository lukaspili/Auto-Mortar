package mvp.compiler;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import flownavigation.path.Path;
import flownavigation.view.Layout;
import mvp.ScreenParam;
import mvp.compiler.extractor.ElementExtractor;
import mvp.compiler.message.Message;
import mvp.compiler.message.MessageDelivery;
import mvp.compiler.model.InjectableVariableElement;
import mvp.compiler.model.spec.BaseViewSpec;
import mvp.compiler.model.spec.ComponentSpec;
import mvp.compiler.model.spec.ModuleSpec;
import mvp.compiler.model.spec.ScreenSpec;
import mvp.compiler.names.ClassNames;
import mvp.compiler.names.Textes;
import mvp.navigation.MVP;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class MvpProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private final Types types;
    private final Elements elements;
    private final Filer filer;
    private final MessageDelivery messageDelivery;
    private final MisunderstoodPoet misunderstoodPoet;

    public MvpProcessingStep(Types types, Elements elements, Filer filer, MessageDelivery messageDelivery) {
        Preconditions.checkNotNull(types);
        Preconditions.checkNotNull(elements);
        Preconditions.checkNotNull(filer);
        Preconditions.checkNotNull(messageDelivery);

        this.types = types;
        this.elements = elements;
        this.filer = filer;
        this.messageDelivery = messageDelivery;

        misunderstoodPoet = new MisunderstoodPoet();
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.<Class<? extends Annotation>>of(MVP.class, mvp.flowpath.MVP.class, mvp.standalone.MVP.class);
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {

        List<ScreenSpec> screenSpecs = new ArrayList<>();

        for (Class<? extends Annotation> annotation : elementsByAnnotation.keySet()) {
            // @MVP can come from flow-navigation or flow-path
            boolean fromFlowNavigation = annotation.equals(MVP.class);

            Set<Element> elements = elementsByAnnotation.get(annotation);
            for (Element element : elements) {
                ElementExtractor elementExtractor = new ElementExtractor(element, annotation, types, this.elements);

                boolean valid = validateElement(elementExtractor);
                if (!valid) {
                    // do not try to build screen for already invalid element
                    continue;
                }

                ClassNames classNames = new ClassNames(elementExtractor.getElement());

                ScreenSpec screenSpec = buildScreen(elementExtractor, classNames);
                Preconditions.checkNotNull(screenSpec);
                screenSpecs.add(screenSpec);
            }
        }

        boolean valid = validateSpecs(screenSpecs);
        if (valid) {
            generateSpecs(screenSpecs);
        }
    }

    private ScreenSpec buildScreen(ElementExtractor elementExtractor, ClassNames classNames) {
        Preconditions.checkNotNull(elementExtractor);
        Preconditions.checkNotNull(classNames);

        ScreenSpec screenSpec = new ScreenSpec(classNames.getScreenClassName(), elementExtractor.getElement());
        screenSpec.setLayout(elementExtractor.getLayout());
        screenSpec.setDaggerComponentTypeName(classNames.getDaggerComponentClassName());

        // screen superclass and layout annotation
        if (elementExtractor.getMvpAnnotationSource() == ElementExtractor.MvpAnnotationSource.FLOW_NAVIGATION) {
            screenSpec.setSuperclassTypeName(ClassName.get(Path.class));
            screenSpec.setLayoutAnnotationClassName(ClassName.get(Layout.class));
        } else if (elementExtractor.getMvpAnnotationSource() == ElementExtractor.MvpAnnotationSource.FLOW_PATH) {
            screenSpec.setSuperclassTypeName(ClassName.get(flow.path.Path.class));
            screenSpec.setLayoutAnnotationClassName(ClassName.get(mvp.flowpath.Layout.class));
        } else if (elementExtractor.getScreenSuperclassTypeMirror() != null) {
            screenSpec.setSuperclassTypeName(TypeName.get(elementExtractor.getScreenSuperclassTypeMirror()));
        }


        if (elementExtractor.getViewTypeMirror() != null) {
            screenSpec.setViewTypeName(TypeName.get(elementExtractor.getViewTypeMirror()));
        } else {
            screenSpec.setViewTypeName(classNames.getBaseViewClassName());

            // generate base view
            BaseViewSpec baseViewSpec = buildBaseView(elementExtractor, classNames);
            Preconditions.checkNotNull(baseViewSpec);
            screenSpec.setBaseViewSpec(baseViewSpec);
        }

        List<InjectableVariableElement> injectableParams = buildInjectableParams(elementExtractor);
        Preconditions.checkNotNull(injectableParams);

        List<InjectableVariableElement> screenParamsMembers = Lists.newArrayList(Collections2.filter(injectableParams, new Predicate<InjectableVariableElement>() {
            @Override
            public boolean apply(InjectableVariableElement input) {
                return input.isScreenParam();
            }
        }));
        screenSpec.setScreenParamMembers(screenParamsMembers);

        ModuleSpec moduleSpec = buildModule(elementExtractor, classNames, injectableParams);
        Preconditions.checkNotNull(moduleSpec);
        screenSpec.setModuleSpec(moduleSpec);

        ComponentSpec componentSpec = buildComponent(elementExtractor, classNames);
        Preconditions.checkNotNull(componentSpec);
        componentSpec.setViewTypeName(screenSpec.getViewTypeName());
        screenSpec.setComponentSpec(componentSpec);

        return screenSpec;
    }

    private BaseViewSpec buildBaseView(ElementExtractor elementExtractor, ClassNames classNames) {
        Preconditions.checkNotNull(elementExtractor);
        Preconditions.checkNotNull(elementExtractor.getViewBaseLayoutTypeMirror());

        BaseViewSpec baseViewSpec = new BaseViewSpec(classNames.getBaseViewClassName());
        baseViewSpec.setSuperclassTypeName(TypeName.get(elementExtractor.getViewBaseLayoutTypeMirror()));
        baseViewSpec.setComponentClassName(classNames.getComponentClassName());
        baseViewSpec.setPresenterClassName(classNames.getPresenterClassName());

        // get the view associated to the presenter via its parameterized type: ViewPresenter<MyView>
        // may be null if @MVP annotated presenter does not yet extend ViewPresenter<MyView>
        if (elementExtractor.getElementParameterizedType() != null) {
            baseViewSpec.setViewTypeName(TypeName.get(elementExtractor.getElementParameterizedType()));
        } else {
            // add warning
            String warning = Textes.getPresenterMustExtendsSuperclassText(elementExtractor.getElement());
            messageDelivery.add(Message.warning(elementExtractor.getElement(), warning));
            baseViewSpec.setPresenterMustExtendsViewPresenterWarning(warning);
        }

        return baseViewSpec;
    }

    private List<InjectableVariableElement> buildInjectableParams(ElementExtractor elementExtractor) {
        List<InjectableVariableElement> injectableParams = new ArrayList<>();
        for (Element enclosedElement : elementExtractor.getElement().getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR &&
                    MoreElements.isAnnotationPresent(enclosedElement, Inject.class)) {
                for (VariableElement variableElement : MoreElements.asExecutable(enclosedElement).getParameters()) {
                    injectableParams.add(new InjectableVariableElement(variableElement, MoreElements.isAnnotationPresent(variableElement, ScreenParam.class)));
                }
            }
        }

        return injectableParams;
    }

    private ModuleSpec buildModule(ElementExtractor elementExtractor, ClassNames classNames, List<InjectableVariableElement> injectableParams) {
        Preconditions.checkNotNull(elementExtractor);
        Preconditions.checkNotNull(classNames);

        ModuleSpec moduleSpec = new ModuleSpec(classNames.getModuleClassName());
        moduleSpec.setPresenterClassName(classNames.getPresenterClassName());
        moduleSpec.setProvidePresenterConstructorParams(injectableParams);

        List<InjectableVariableElement> methodParams = Lists.newArrayList(Collections2.filter(injectableParams, new Predicate<InjectableVariableElement>() {
            @Override
            public boolean apply(InjectableVariableElement input) {
                return !input.isScreenParam();
            }
        }));
        moduleSpec.setProvidePresenterParams(methodParams);

        return moduleSpec;
    }

    private ComponentSpec buildComponent(ElementExtractor elementExtractor, ClassNames classNames) {
        Preconditions.checkNotNull(elementExtractor);
        Preconditions.checkNotNull(classNames);

        ComponentSpec componentSpec = new ComponentSpec(classNames.getComponentClassName());
        componentSpec.setParentTypeName(TypeName.get(elementExtractor.getParentComponentTypeMirror()));
        componentSpec.setModuleTypeName(classNames.getModuleClassName());

        return componentSpec;
    }

    private boolean validateElement(ElementExtractor elementExtractor) {
        Preconditions.checkNotNull(elementExtractor);

        if (elementExtractor.getViewTypeMirror() != null && elementExtractor.getViewBaseLayoutTypeMirror() != null) {
            messageDelivery.add(Message.error(elementExtractor.getElement(), "@MVP cannot have both view and baseViewLayout defined in the same time."));
            return false;
        }

        if (elementExtractor.getViewTypeMirror() == null && elementExtractor.getViewBaseLayoutTypeMirror() == null) {
            messageDelivery.add(Message.error(elementExtractor.getElement(), "@MVP requires view or baseViewLayout to be defined."));
            return false;
        }

        if (elementExtractor.getParentComponentTypeMirror() == null) {
            messageDelivery.add(Message.error(elementExtractor.getElement(), "@MVP requires parentComponent to be defined."));
            return false;
        }

        if (elementExtractor.getLayout() == 0) {
            messageDelivery.add(Message.error(elementExtractor.getElement(), "@MVP requires layout to be defined."));
            return false;
        }

        return true;
    }

    private boolean validateSpecs(List<ScreenSpec> screenSpecs) {
        return true;
    }

    private void generateSpecs(List<ScreenSpec> screenSpecs) {
        for (ScreenSpec screenSpec : screenSpecs) {
            TypeSpec typeSpec = misunderstoodPoet.compose(screenSpec);
            JavaFile javaFile = JavaFile.builder(screenSpec.getClassName().packageName(), typeSpec).build();

            try {
                javaFile.writeTo(filer);
            } catch (Exception e) {
                StringWriter stackTrace = new StringWriter();
                e.printStackTrace(new PrintWriter(stackTrace));
                messageDelivery.add(Message.error(screenSpec.getElement(), "Unable to generate classes for %s. %s", screenSpec.getClassName().simpleName(), stackTrace));
            }
        }
    }


}
