package mvp.compiler.processingstep;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.inject.Inject;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import mvp.MVP;
import mvp.ScreenParam;
import mvp.compiler.MisunderstoodPoet;
import mvp.compiler.extractor.ElementExtractor;
import mvp.compiler.message.Message;
import mvp.compiler.message.MessageDelivery;
import mvp.compiler.model.Configuration;
import mvp.compiler.model.InjectableVariableElement;
import mvp.compiler.model.spec.ConfigSpec;
import mvp.compiler.model.spec.ModuleSpec;
import mvp.compiler.model.spec.ScreenAnnotationSpec;
import mvp.compiler.model.spec.ScreenSpec;
import mvp.compiler.names.ClassNames;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class MvpProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private final Types types;
    private final Elements elements;
    private final Filer filer;
    private final MessageDelivery messageDelivery;
    private final ProcessingStepsBus processingStepsBus;
    private final MisunderstoodPoet misunderstoodPoet;

    public MvpProcessingStep(Types types, Elements elements, Filer filer, MessageDelivery messageDelivery, ProcessingStepsBus processingStepsBus) {
        Preconditions.checkNotNull(types);
        Preconditions.checkNotNull(elements);
        Preconditions.checkNotNull(filer);
        Preconditions.checkNotNull(messageDelivery);
        Preconditions.checkNotNull(processingStepsBus);

        this.types = types;
        this.elements = elements;
        this.filer = filer;
        this.messageDelivery = messageDelivery;
        this.processingStepsBus = processingStepsBus;

        misunderstoodPoet = new MisunderstoodPoet();
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.<Class<? extends Annotation>>of(MVP.class);
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        // get user configuration, or default one if none provided
        Configuration configuration = processingStepsBus.getConfiguration();
        if (configuration == null) {
            configuration = Configuration.defaultConfig();
        }

        List<ScreenSpec> screenSpecs = new ArrayList<>();
        for (Class<? extends Annotation> annotation : elementsByAnnotation.keySet()) {
            Set<Element> elements = elementsByAnnotation.get(annotation);
            for (Element element : elements) {
                ElementExtractor elementExtractor = new ElementExtractor(element, annotation, types, this.elements);

                boolean valid = validateElement(elementExtractor);
                if (!valid) {
                    // do not try to build screen for already invalid element
                    continue;
                }

                ClassNames classNames = new ClassNames(elementExtractor.getElement());

                ScreenSpec screenSpec = buildScreen(elementExtractor, classNames, configuration);
                Preconditions.checkNotNull(screenSpec);
                screenSpecs.add(screenSpec);
            }
        }

        ConfigSpec configSpec = buildConfig(configuration);
        Preconditions.checkNotNull(configSpec, "Config spec is null");

        boolean valid = validateSpecs(screenSpecs, configSpec);
        if (valid) {
            generateSpecs(screenSpecs, configSpec);
        }
    }

    private ConfigSpec buildConfig(Configuration configuration) {
        ConfigSpec configSpec = new ConfigSpec(ClassNames.mvpConfig());
        configSpec.setDaggerServiceName(configuration.getDaggerServiceName());
        return configSpec;
    }

    private ScreenSpec buildScreen(ElementExtractor elementExtractor, ClassNames classNames, Configuration configuration) {
        Preconditions.checkNotNull(elementExtractor);
        Preconditions.checkNotNull(classNames);

        ScreenSpec screenSpec = new ScreenSpec(classNames.getScreenClassName(), elementExtractor.getElement());
        screenSpec.setDaggerComponentTypeName(classNames.getDaggerComponentClassName());
        screenSpec.setPresenterTypeName(classNames.getPresenterClassName());

        // screen superclass, first let's see if user specified a superclass on @MVP
        // otherwise use config
        // if config provides null, screen will have no superclass
        if (elementExtractor.getScreenSuperclassTypeMirror() != null) {
            screenSpec.setSuperclassTypeName(ClassName.get(elementExtractor.getScreenSuperclassTypeMirror()));
        } else if (configuration.getScreenSuperclassTypeName() != null) {
            screenSpec.setSuperclassTypeName(configuration.getScreenSuperclassTypeName());
        }

        // screen annotations
        List<ScreenAnnotationSpec> annotationSpecs = new ArrayList<>();
        for (AnnotationMirror annotationTypeMirror : elementExtractor.getScreenAnnotationsMirrors()) {
            Element e = MoreTypes.asElement(annotationTypeMirror.getAnnotationType());
            TypeElement te = MoreElements.asType(e);
            ScreenAnnotationSpec spec = new ScreenAnnotationSpec(ClassName.get(te));
            annotationSpecs.add(spec);

            Map<? extends ExecutableElement, ? extends AnnotationValue> map = annotationTypeMirror.getElementValues();
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : map.entrySet()) {
                spec.getMembers().put(entry.getKey().getSimpleName().toString(), entry.getValue());
            }
        }

        if (!annotationSpecs.isEmpty()) {
            screenSpec.setAnnotationSpecs(annotationSpecs);
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

        return screenSpec;
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

//    private ComponentSpec buildComponent(ElementExtractor elementExtractor, ClassNames classNames, List<WithInjectorExtractor> withInjectorExtractors, List<WithComponentExtractor> withComponentExtractors) {
//        Preconditions.checkNotNull(elementExtractor);
//        Preconditions.checkNotNull(classNames);
//
//        ComponentSpec componentSpec = new ComponentSpec(classNames.getComponentClassName());
//        componentSpec.setModuleTypeName(classNames.getModuleClassName());
//
//        // parent can be comp interface or presenter
//        Element e = MoreTypes.asElement(elementExtractor.getParentTypeMirror());
//        if (MoreElements.isAnnotationPresent(e, Component.class)) {
//            componentSpec.setParentTypeName(TypeName.get(elementExtractor.getParentTypeMirror()));
//        } else if (MoreElements.isAnnotationPresent(e, MVP.class)) {
//            ClassNames elementClassNames = new ClassNames(e);
//            componentSpec.setParentTypeName(elementClassNames.getComponentClassName());
//        } else {
//            throw new IllegalStateException("@MVP parent must be a dagger 2 component or an @MVP annotated presenter");
//        }
//
//        List<WithInjectorSpec> withInjectorSpecs = new ArrayList<>();
//        if (withInjectorExtractors != null) {
//            for (WithInjectorExtractor extractor : withInjectorExtractors) {
//                for (TypeMirror typeMirror : extractor.getTypeMirrors()) {
//                    if (types.isSameType(elementExtractor.getElement().asType(), typeMirror)) {
////                    String name = extractor.getElement().getSimpleName().toString().toLowerCase();
//                        withInjectorSpecs.add(new WithInjectorSpec("view", TypeName.get(extractor.getElement().asType())));
//                    }
//                }
//            }
//        }
//        componentSpec.setWithInjectorSpecs(withInjectorSpecs);
//
//        List<WithComponentSpec> withComponentSpecs = new ArrayList<>();
//        if (withComponentExtractors != null) {
//            for (WithComponentExtractor extractor : withComponentExtractors) {
//                for (TypeMirror typeMirror : extractor.getTypeMirrors()) {
//                    if (types.isSameType(elementExtractor.getElement().asType(), typeMirror)) {
//                        String name = extractor.getElement().getSimpleName().toString();
//                        name = WordUtils.uncapitalize(name);
//                        withComponentSpecs.add(new WithComponentSpec(name, TypeName.get(extractor.getElement().asType())));
//                    }
//                }
//            }
//        }
//        componentSpec.setWithComponentSpecs(withComponentSpecs);
//
//        return componentSpec;
//    }

    private boolean validateElement(ElementExtractor elementExtractor) {
        Preconditions.checkNotNull(elementExtractor);

        if (elementExtractor.getParentTypeMirror() == null) {
            messageDelivery.add(Message.error(elementExtractor.getElement(), "@MVP requires parentComponent to be defined."));
            return false;
        }

        return true;
    }

    private boolean validateSpecs(List<ScreenSpec> screenSpecs, ConfigSpec configSpec) {
        Preconditions.checkNotNull(configSpec.getDaggerServiceName(), "Config dagger service name is null");

        // TODO: add validation
        return true;
    }

    private void generateSpecs(List<ScreenSpec> screenSpecs, ConfigSpec configSpec) {
        for (ScreenSpec screenSpec : screenSpecs) {
            TypeSpec typeSpec = misunderstoodPoet.compose(screenSpec);
            JavaFile javaFile = JavaFile.builder(screenSpec.getClassName().packageName(), typeSpec).build();
            write(javaFile, screenSpec.getElement());
        }

        // do not generate config classe twice, if there are multiple rounds
        if (!processingStepsBus.isConfigGenerated()) {
            processingStepsBus.setConfigGenerated(true);

            TypeSpec typeSpec = misunderstoodPoet.compose(configSpec);
            JavaFile javaFile = JavaFile.builder(configSpec.getClassName().packageName(), typeSpec).build();
            write(javaFile, null);
        }
    }

    private void write(JavaFile javaFile, Element element) {
        try {
            javaFile.writeTo(filer);
        } catch (Exception e) {
            StringWriter stackTrace = new StringWriter();
            e.printStackTrace(new PrintWriter(stackTrace));
            messageDelivery.add(Message.error(element, "Unable to generate class for %s. %s", javaFile.typeSpec.name, stackTrace));
        }
    }


}
