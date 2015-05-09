package mvp.compiler;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;

import mvp.compiler.message.MessageDelivery;
import mvp.compiler.processingstep.ConfigurationProcessingStep;
import mvp.compiler.processingstep.InjectableWithProcessingStep;
import mvp.compiler.processingstep.MvpProcessingStep;
import mvp.compiler.processingstep.ProcessingStepsBus;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends BasicAnnotationProcessor {

    private MessageDelivery messageDelivery = new MessageDelivery();
    private ProcessingStepsBus processingStepsBus = new ProcessingStepsBus();

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return ImmutableSet.of(
                new ConfigurationProcessingStep(messageDelivery, processingStepsBus),
                new InjectableWithProcessingStep(messageDelivery, processingStepsBus),
                new MvpProcessingStep(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getFiler(), messageDelivery, processingStepsBus)
        );
    }

    @Override
    protected void postProcess() {
        messageDelivery.deliver(processingEnv.getMessager());
    }

//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
//
//        for (Element element : env.getElementsAnnotatedWith(mvp.navigation.MVP.class)) {
//            try {
//                parseMortarMVP(element, flownavigation.path.Path.class);
//            } catch (Exception e) {
//                logParsingError(element, mvp.navigation.MVP.class, e);
//            }
//        }
//
//        for (Element element : env.getElementsAnnotatedWith(mvp.flowpath.MVP.class)) {
//            try {
//                parseMortarMVP(element, flow.path.Path.class);
//            } catch (Exception e) {
//                logParsingError(element, mvp.flowpath.MVP.class, e);
//            }
//        }
//
//        return false;
//    }
//
//    private void parseMortarMVP(Element element, Class screenSuperclass) throws Exception {
//        TypeMirror elementType = element.asType();
//        if (elementType.getKind() != TypeKind.DECLARED) {
//            error(element, "@MVP must be used on a class only");
//            return;
//        }
//
//
//        TypeMirror viewTypeMirror = Utils.getTypeMirror(element, MVP.class, "view");
//        TypeMirror baseViewTypeMirror = Utils.getTypeMirror(element, MVP.class, "baseView");
//
//        if (viewTypeMirror != null && baseViewTypeMirror != null) {
//            error(element, "@MVP cannot have both view and baseView defined in the same time");
//            return;
//        }
//
//        if (viewTypeMirror == null && baseViewTypeMirror == null) {
//            error(element, "@MVP requires view or baseView to be defined");
//            return;
//        }
//
//        List<String> warnings = new ArrayList<>();
//
//        List<TypeSpec> typeSpecs = new ArrayList<>();
//        TypeName viewTypeName;
//
//        if (viewTypeMirror != null) {
//            // use existing view
//            viewTypeName = ClassName.get(viewTypeMirror);
//        } else {
//            // generate new base view
//            viewTypeName = viewClassName(element);
//
//            // get the view associated to the presenter via its parameterized type: ViewPresenter<MyView>
//            TypeMirror viewPresenterParameterizedType = getViewPresenterParameterizedType(element);
//            if (viewPresenterParameterizedType == null) {
//                warnings.add(getPresenterMustExtendsSuperclassWarning(element));
//            }
//
//            // view
//            TypeSpec viewTypeSpec = createView(element, baseViewTypeMirror, viewPresenterParameterizedType);
//            typeSpecs.add(viewTypeSpec);
//        }
//
//        TypeMirror parentComponentTypeMirror = Utils.getTypeMirror(element, MVP.class, "parentComponent");
//        if (parentComponentTypeMirror == null) {
//            error(element, "@MVP must declare parentComponent member");
//            return;
//        }
//        TypeName parentComponentTypeName = TypeName.get(parentComponentTypeMirror);
//
//        // module
//        List<InjectableVariableElement> injectableInjectableVariableElements = new ArrayList<>();
//        for (Element enclosedElement : element.getEnclosedElements()) {
//            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR &&
//                    MoreElements.isAnnotationPresent(enclosedElement, Inject.class)) {
//                for (VariableElement variableElement : MoreElements.asExecutable(enclosedElement).getParameters()) {
//                    message("Injectable method param %s %s", variableElement.getSimpleName(), variableElement.asType());
//                    injectableInjectableVariableElements.add(new InjectableVariableElement(variableElement, MoreElements.isAnnotationPresent(variableElement, ScreenParam.class)));
//                }
//            }
//        }
//
//        // module
//        typeSpecs.add(createModule(element, injectableInjectableVariableElements));
//
//        // component
//        typeSpecs.add(createComponent(element, parentComponentTypeName, viewTypeName));
//
//        TypeMirror superclassTypeMirror = Utils.getTypeMirror(element, MVP.class, "baseView");
//
//        // screen
//        TypeSpec screenTypeSpec = createScreen(element, parentComponentTypeName, typeSpecs, injectableInjectableVariableElements, screenSuperclass);
//
//        // generate screen
//        generate(element, screenTypeSpec);
//
//        // warnings
//        for (String string : warnings) {
//            warning(string);
//        }
//    }
//
//    private String getPresenterMustExtendsSuperclassWarning(Element element) {
//        return String.format("%s must now extend from %s<? extends android.view.View>", element.getSimpleName(), ViewPresenter.class.getCanonicalName());
//    }
//
//    private Optional<DeclaredType> getSuperclassDeclaredType(Element element) {
//        try {
//            return MoreTypes.nonObjectSuperclass(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), MoreTypes.asDeclared(element.asType()));
//        } catch (Exception e) {
//            warning("Invalid superclass for %s", element);
//            return Optional.absent();
//        }
//    }
//
//    /**
//     * Iterate over @MVP annotated presenter superclasses until it finds the ViewPresenter&lt;?&gt; parameterized type
//     * <p/>
//     * Current limitation is that it does not support fully multiple parameterized types
//     * Each superclass must declare the presenter associated view as its first parameterized type
//     *
//     * @param element the @MVP annotated presenter element
//     * @return the TypeMirror of the ViewPresenter&lt;?&gt; parameterized type or null if none
//     */
//    private TypeMirror getViewPresenterParameterizedType(Element element) {
//        Element superclassElement = element;
//
//        Optional<DeclaredType> declaredTypeResult;
//        while ((declaredTypeResult = getSuperclassDeclaredType(superclassElement)).isPresent()) {
//            superclassElement = declaredTypeResult.get().asElement();
//
//            if (!declaredTypeResult.get().getTypeArguments().isEmpty()) {
//                // take the first parameterized type to associate to the view
//                return declaredTypeResult.get().getTypeArguments().get(0);
//            }
//        }
//
//        return null;
//    }
//
//    private TypeSpec createModule(Element element, List<InjectableVariableElement> injectableInjectableVariableElements) {
//        ClassName presenterClassName = presenterClassName(element);
//
//        MethodSpec.Builder builder = MethodSpec.methodBuilder("providePresenter")
//                .addModifiers(Modifier.PUBLIC)
//                .returns(presenterClassName)
//                .addAnnotation(Provides.class)
//                .addAnnotation(createScopeAnnotationSpec(element));
//
//        StringBuilder params = new StringBuilder();
//        for (InjectableVariableElement injectableVariableElement : injectableInjectableVariableElements) {
//            if (!injectableVariableElement.isScreenParam()) {
//                builder.addParameter(TypeName.get(injectableVariableElement.getElement().asType()), injectableVariableElement.getElement().getSimpleName().toString());
//            }
//            params.append(injectableVariableElement.getElement().getSimpleName()).append(", ");
//        }
//
//        if (params.length() > 0) {
//            params.delete(params.length() - 2, params.length());
//        }
//
//        builder.addStatement("return new $T($L)", presenterClassName, params);
//
//        return TypeSpec.classBuilder(getModuleTypeSpecName())
//                .addModifiers(Modifier.PUBLIC)
//                .addAnnotation(Module.class)
//                .addMethod(builder.build())
//                .build();
//    }
//
//    private TypeSpec createScreen(Element element, TypeName parentComponentTypeName, List<TypeSpec> typeSpecs, List<InjectableVariableElement> injectableInjectableVariableElements, Class superclass) {
//        MVP mvp = element.getAnnotation(MVP.class);
//        AnnotationSpec layoutAnnotationSpec = AnnotationSpec.builder(Layout.class)
//                .addMember("value", "$L", mvp.layout())
//                .build();
//
//        MethodSpec createComponentMethod = MethodSpec.methodBuilder("createComponent")
//                .addModifiers(Modifier.PUBLIC)
//                .returns(Object.class)
//                .addParameter(parentComponentTypeName, "parentComponent")
//                .addAnnotation(Override.class)
//                .addCode("return $T.builder()\n\t" +
//                        ".component(parentComponent)\n\t" +
//                        ".module(new Module())\n\t" +
//                        ".build();\n", daggerComponentClassName(element))
//                .build();
//
//        String name = getScreenTypeSpecName(element);
//        TypeSpec.Builder builder = TypeSpec.classBuilder(name)
//                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ComponentFactory.class), parentComponentTypeName))
//                .superclass(ClassName.get(superclass))
//                .addAnnotation(createGeneratedAnnotationSpec())
//                .addAnnotation(layoutAnnotationSpec)
//                .addMethod(createComponentMethod);
//
//        for (TypeSpec typeSpec : typeSpecs) {
//            builder.addType(typeSpec);
//            message("Will generate %s with %s", name, typeSpec.name);
//        }
//
//        List<VariableElement> constructorParameters = new ArrayList<>();
//        for (InjectableVariableElement injectableVariableElement : injectableInjectableVariableElements) {
//            if (injectableVariableElement.isScreenParam()) {
//                builder.addField(FieldSpec.builder(TypeName.get(injectableVariableElement.getElement().asType()), injectableVariableElement.getElement().getSimpleName().toString())
//                        .addModifiers(Modifier.PRIVATE)
//                        .build());
//                constructorParameters.add(injectableVariableElement.getElement());
//            }
//        }
//
//        if (!constructorParameters.isEmpty()) {
//            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
//                    .addModifiers(Modifier.PUBLIC);
//            for (VariableElement variableElement : constructorParameters) {
//                constructorBuilder.addParameter(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString());
//                constructorBuilder.addStatement("this.$L = $L", variableElement.getSimpleName().toString(), variableElement.getSimpleName().toString());
//            }
//            builder.addMethod(constructorBuilder.build());
//        }
//
//        return builder.build();
//    }
//
//    private void generate(Element element, TypeSpec typeSpec) throws Exception {
//        JavaFile javaFile = JavaFile.builder(getPackage(element), typeSpec).build();
//        javaFile.writeTo(processingEnv.getFiler());
//        message("Generated %s", typeSpec.name);
//    }
//
//    private AnnotationSpec createGeneratedAnnotationSpec() {
//        return AnnotationSpec.builder(Generated.class)
//                .addMember("value", "$S", getClass().getName())
//                .build();
//    }
//
//    private TypeSpec createComponent(Element element, TypeName parentComponentTypeName, TypeName viewTypeName) {
//        AnnotationSpec.Builder componentAnnotationBuilder = AnnotationSpec.builder(Component.class)
//                .addMember("dependencies", "$T.class", parentComponentTypeName)
//                .addMember("modules", "$T.class", className(element, getModuleTypeSpecName()));
//
//        return TypeSpec.interfaceBuilder(getComponentTypeSpecName())
//                .addModifiers(Modifier.PUBLIC)
//                .addSuperinterface(parentComponentTypeName)
//                .addAnnotation(componentAnnotationBuilder.build())
//                .addAnnotation(createScopeAnnotationSpec(element))
//                .addMethod(MethodSpec.methodBuilder("inject")
//                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
//                        .addParameter(viewTypeName, "inject")
//                        .build())
//                .build();
//    }
//
//    private TypeSpec createView(Element element, TypeMirror baseViewTypeMirror, TypeMirror viewPresenterParameterizedType) {
//
//        ParameterSpec contextParameterSpec = ParameterSpec.builder(ClassName.get("android.content", "Context"), "context")
//                .build();
//        ParameterSpec attributeSetParameterSpec = ParameterSpec.builder(ClassName.get("android.util", "AttributeSet"), "attrs")
//                .build();
//        ParameterSpec defStyleAttrParameterSpec = ParameterSpec.builder(TypeName.INT, "defStyleAttr")
//                .build();
//
//        MethodSpec constructor1 = MethodSpec.constructorBuilder()
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(contextParameterSpec)
//                .addStatement("super($L)", "context")
//                .addStatement("init($L)", "context")
//                .build();
//
//        MethodSpec constructor2 = MethodSpec.constructorBuilder()
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(contextParameterSpec)
//                .addParameter(attributeSetParameterSpec)
//                .addStatement("super($L, $L)", "context", "attrs")
//                .addStatement("init($L)", "context")
//                .build();
//
//        MethodSpec constructor3 = MethodSpec.constructorBuilder()
//                .addModifiers(Modifier.PUBLIC)
//                .addParameter(contextParameterSpec)
//                .addParameter(attributeSetParameterSpec)
//                .addParameter(defStyleAttrParameterSpec)
//                .addStatement("super($L, $L, $L)", "context", "attrs", "defStyleAttr")
//                .addStatement("init($L)", "context")
//                .build();
//
//        MethodSpec init = MethodSpec.methodBuilder("init")
//                .addModifiers(Modifier.PROTECTED)
//                .addParameter(contextParameterSpec)
//                .addStatement("(($T)$L.getSystemService($T.SERVICE_NAME)).inject(this)", componentClassName(element), "context", DaggerService.class)
//                .build();
//
////        String takeViewStatement = hasPresenterSuperclass ? "presenter.takeView(()this)" : String.format(");
////                String dropViewStatement = hasPresenterSuperclass ? "presenter.dropView(this)" : String.format("// presenter.dropView(this) -> %s", getPresenterMustExtendsSuperclassWarning(element));
//
//        //TODO: Generate constructor4 for Android API 21
//
//        MethodSpec.Builder onAttachedToWindowBuilder = MethodSpec.methodBuilder("onAttachedToWindow")
//                .addModifiers(Modifier.PUBLIC)
//                .returns(void.class)
//                .addAnnotation(Override.class)
//                .addStatement("super.onAttachedToWindow()");
//        if (viewPresenterParameterizedType != null) {
//            onAttachedToWindowBuilder.addStatement("presenter.takeView(($T)this)", TypeName.get(viewPresenterParameterizedType));
//        } else {
//            onAttachedToWindowBuilder.addStatement("// presenter.takeView(this) -> $S", getPresenterMustExtendsSuperclassWarning(element));
//        }
//
//        MethodSpec.Builder onDetachedFromWindowBuilder = MethodSpec.methodBuilder("onDetachedFromWindow")
//                .addModifiers(Modifier.PUBLIC)
//                .returns(void.class)
//                .addAnnotation(Override.class);
//        if (viewPresenterParameterizedType != null) {
//            onDetachedFromWindowBuilder.addStatement("presenter.dropView(($T)this)", TypeName.get(viewPresenterParameterizedType));
//        } else {
//            onDetachedFromWindowBuilder.addStatement("// presenter.dropView(this) -> $S", getPresenterMustExtendsSuperclassWarning(element));
//        }
//        onDetachedFromWindowBuilder.addStatement("super.onDetachedFromWindow()");
//
//        return TypeSpec.classBuilder(getViewTypeSpecName())
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
//                .superclass(ClassName.get(baseViewTypeMirror))
//                .addField(FieldSpec.builder(presenterClassName(element), "presenter")
//                        .addModifiers(Modifier.PROTECTED)
//                        .addAnnotation(Inject.class)
//                        .build())
//                .addMethod(constructor1)
//                .addMethod(constructor2)
//                .addMethod(constructor3)
//                .addMethod(init)
//                .addMethod(MethodSpec.methodBuilder("onFinishInflate")
//                        .addModifiers(Modifier.PUBLIC)
//                        .returns(void.class)
//                        .addAnnotation(Override.class)
//                        .addStatement("super.onFinishInflate()")
//                        .addStatement("$T.inject(this)", ClassName.get("butterknife", "ButterKnife"))
//                        .build())
//                .addMethod(onAttachedToWindowBuilder.build())
//                .addMethod(onDetachedFromWindowBuilder.build())
//                .build();
//    }
//
//    private AnnotationSpec createScopeAnnotationSpec(Element element) {
//        return AnnotationSpec.builder(ScreenScope.class)
//                .addMember("value", "$T.class", componentClassName(element))
//                .build();
//    }
//
//    private ClassName daggerComponentClassName(Element element) {
//        return ClassName.get(getPackage(element), String.format("Dagger%s_%s", getScreenTypeSpecName(element), getComponentTypeSpecName()));
//    }
//
//    private ClassName viewClassName(Element element) {
//        return innerClassName(element, getViewTypeSpecName());
//    }
//
//    private ClassName presenterClassName(Element element) {
//        return className(element, element.getSimpleName().toString());
//    }
//
//    private ClassName componentClassName(Element element) {
//        return innerClassName(element, getComponentTypeSpecName());
//    }
//
//    private ClassName innerClassName(Element element, String name) {
//        return className(element, "%s.%s", getScreenTypeSpecName(element), name);
//    }
//
//    private ClassName className(Element element, String name, Object... args) {
//        return ClassName.get(getPackage(element), args.length > 0 ? String.format(name, args) : name);
//    }
//
//    private String getPackage(Element element) {
//        return element.getEnclosingElement().toString();
//    }
//
//    private String getScreenTypeSpecName(Element element) {
//        String name = element.getSimpleName().toString();
//
//        // try to remove the "Presenter" at the end if any
//        int res = name.lastIndexOf("Presenter");
//        if (res != -1 && res + "Presenter".length() == name.length()) {
//            name = name.substring(0, res);
//        }
//        return String.format("MVP_%sScreen", name);
//    }
//
//    private String getComponentTypeSpecName() {
//        return "Component";
//    }
//
//    private String getModuleTypeSpecName() {
//        return "Module";
//    }
//
//    private String getPresenterTypeSpecName() {
//        return "Presenter";
//    }
//
//    private String getViewTypeSpecName() {
//        return "View";
//    }
//
//    private void logParsingError(Element element, Class<? extends Annotation> annotation, Exception e) {
//        StringWriter stackTrace = new StringWriter();
//        e.printStackTrace(new PrintWriter(stackTrace));
//        error(element, "Unable to parse @%s binding.\n\n%s", annotation.getSimpleName(), stackTrace);
//    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        Set<String> types = new LinkedHashSet<>();
//        types.add(MVP.class.getCanonicalName());
//
//        return types;
//    }
}
