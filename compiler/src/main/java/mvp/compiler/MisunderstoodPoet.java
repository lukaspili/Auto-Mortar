package mvp.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import dagger.Component;
import dagger.Module;
import dagger.Provides;
import mvp.ComponentFactory;
import mvp.ScreenScope;
import mvp.compiler.model.InjectableVariableElement;
import mvp.compiler.model.spec.BaseViewSpec;
import mvp.compiler.model.spec.ComponentSpec;
import mvp.compiler.model.spec.ConfigSpec;
import mvp.compiler.model.spec.ModuleSpec;
import mvp.compiler.model.spec.ScreenAnnotationSpec;
import mvp.compiler.model.spec.ScreenSpec;
import mvp.compiler.model.spec.WithComponentSpec;
import mvp.compiler.model.spec.WithInjectorSpec;
import mvp.compiler.names.ClassNames;

/**
 * Actually it generates readable and understandable code!
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class MisunderstoodPoet {

    private final static String CONFIG_DAGGERSERVICENAME = "DAGGER_SERVICE_NAME";

    public TypeSpec compose(ScreenSpec screenSpec) {
        TypeSpec.Builder screenTypeSpecBuilder = createScreenBuilder(screenSpec);

        if (screenSpec.getBaseViewSpec() != null) {
            screenTypeSpecBuilder.addType(composeBaseView(screenSpec.getBaseViewSpec()));
        }

        AnnotationSpec scopeAnnotationSpec = AnnotationSpec.builder(ScreenScope.class)
                .addMember("value", "$T.class", screenSpec.getPresenterTypeName())
                .build();

        screenTypeSpecBuilder.addType(composeModule(screenSpec.getModuleSpec(), scopeAnnotationSpec));
        screenTypeSpecBuilder.addType(composeComponent(screenSpec.getComponentSpec(), scopeAnnotationSpec));

        // and compose!
        return screenTypeSpecBuilder.build();
    }

    public TypeSpec composeBaseView(BaseViewSpec baseViewSpec) {
        ParameterSpec contextParameterSpec = ParameterSpec.builder(ClassNames.context(), "context")
                .build();
        ParameterSpec attributeSetParameterSpec = ParameterSpec.builder(ClassNames.attributeSet(), "attrs")
                .build();
        ParameterSpec defStyleAttrParameterSpec = ParameterSpec.builder(TypeName.INT, "defStyleAttr")
                .build();

        MethodSpec constructor1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextParameterSpec)
                .addStatement("super($L)", "context")
                .addStatement("init($L)", "context")
                .build();

        MethodSpec constructor2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextParameterSpec)
                .addParameter(attributeSetParameterSpec)
                .addStatement("super($L, $L)", "context", "attrs")
                .addStatement("init($L)", "context")
                .build();

        MethodSpec constructor3 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(contextParameterSpec)
                .addParameter(attributeSetParameterSpec)
                .addParameter(defStyleAttrParameterSpec)
                .addStatement("super($L, $L, $L)", "context", "attrs", "defStyleAttr")
                .addStatement("init($L)", "context")
                .build();

        MethodSpec init = MethodSpec.methodBuilder("init")
                .addModifiers(Modifier.PROTECTED)
                .addParameter(contextParameterSpec)
                .addStatement("(($T)$L.getSystemService($L.$L)).inject(this)", baseViewSpec.getComponentClassName(), "context", ClassNames.mvpConfig(), CONFIG_DAGGERSERVICENAME)
                .build();

        //TODO: Generate constructor4 for Android API 21

        MethodSpec.Builder onAttachedToWindowBuilder = MethodSpec.methodBuilder("onAttachedToWindow")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addStatement("super.onAttachedToWindow()");
        if (baseViewSpec.getViewTypeName() != null) {
            onAttachedToWindowBuilder.addStatement("presenter.takeView(($T)this)", baseViewSpec.getViewTypeName());
        } else {
            onAttachedToWindowBuilder.addStatement("// presenter.takeView(this) -> $S", baseViewSpec.getPresenterMustExtendsViewPresenterWarning());
        }

        MethodSpec.Builder onDetachedFromWindowBuilder = MethodSpec.methodBuilder("onDetachedFromWindow")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class);
        if (baseViewSpec.getViewTypeName() != null) {
            onDetachedFromWindowBuilder.addStatement("presenter.dropView(($T)this)", baseViewSpec.getViewTypeName());
        } else {
            onDetachedFromWindowBuilder.addStatement("// presenter.dropView(this) -> $S", baseViewSpec.getPresenterMustExtendsViewPresenterWarning());
        }
        onDetachedFromWindowBuilder.addStatement("super.onDetachedFromWindow()");

        TypeSpec.Builder builder = TypeSpec.classBuilder(baseViewSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
                .superclass(baseViewSpec.getSuperclassTypeName())
                .addField(FieldSpec.builder(baseViewSpec.getPresenterClassName(), "presenter")
                        .addModifiers(Modifier.PROTECTED)
                        .addAnnotation(Inject.class)
                        .build())
                .addMethod(constructor1)
                .addMethod(constructor2)
                .addMethod(constructor3)
                .addMethod(init)
                .addMethod(onAttachedToWindowBuilder.build())
                .addMethod(onDetachedFromWindowBuilder.build());

        // onFinishInflate
        if (baseViewSpec.isButterknife()) {
            MethodSpec onFinishInflateMethodSpec = MethodSpec.methodBuilder("onFinishInflate")
                    .addModifiers(Modifier.PROTECTED)
                    .returns(void.class)
                    .addAnnotation(Override.class)
                    .addStatement("super.onFinishInflate()")
                    .addStatement("$T.inject(this)", ClassNames.butterknife())
                    .build();
            builder.addMethod(onFinishInflateMethodSpec);
        }

        return builder.build();
    }

    public TypeSpec composeModule(ModuleSpec moduleSpec, AnnotationSpec screenScopeAnnotationSpec) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("providePresenter")
                .addModifiers(Modifier.PUBLIC)
                .returns(moduleSpec.getPresenterClassName())
                .addAnnotation(Provides.class)
                .addAnnotation(screenScopeAnnotationSpec);

        for (InjectableVariableElement injectableVariableElement : moduleSpec.getProvidePresenterParams()) {
            builder.addParameter(TypeName.get(injectableVariableElement.getElement().asType()), injectableVariableElement.getElement().getSimpleName().toString());
        }

        StringBuilder params = new StringBuilder();
        for (InjectableVariableElement injectableVariableElement : moduleSpec.getProvidePresenterConstructorParams()) {
            params.append(injectableVariableElement.getElement().getSimpleName()).append(", ");
        }
        if (params.length() > 0) {
            params.delete(params.length() - 2, params.length());
        }
        builder.addStatement("return new $T($L)", moduleSpec.getPresenterClassName(), params);

        return TypeSpec.classBuilder(moduleSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Module.class)
                .addMethod(builder.build())
                .build();
    }

    private TypeSpec composeComponent(ComponentSpec componentSpec, AnnotationSpec scopeAnnotationSpec) {
        AnnotationSpec componentAnnotatioSpec = AnnotationSpec.builder(Component.class)
                .addMember("dependencies", "$T.class", componentSpec.getParentTypeName())
                .addMember("modules", "$T.class", componentSpec.getModuleTypeName())
                .build();

        TypeSpec.Builder builder = TypeSpec.interfaceBuilder(componentSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(componentSpec.getParentTypeName())
                .addAnnotation(componentAnnotatioSpec)
                .addAnnotation(scopeAnnotationSpec)
                .addMethod(MethodSpec.methodBuilder("inject")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(componentSpec.getViewTypeName(), "view")
                        .build());

        for (WithInjectorSpec withInjectorSpec : componentSpec.getWithInjectorSpecs()) {
            builder.addMethod(MethodSpec.methodBuilder("inject")
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addParameter(withInjectorSpec.getTypeName(), withInjectorSpec.getName())
                    .build());
        }

        for (WithComponentSpec withComponentSpec : componentSpec.getWithComponentSpecs()) {
            builder.addMethod(MethodSpec.methodBuilder(withComponentSpec.getName())
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .returns(withComponentSpec.getTypeName())
                    .build());
        }

        return builder.build();
    }

    private TypeSpec.Builder createScreenBuilder(ScreenSpec screenSpec) {
        AnnotationSpec generatedAnnotationSpec = AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", AnnotationProcessor.class.getName())
                .build();

        // static getComponent(Context)
        MethodSpec getComponentMethod = MethodSpec.methodBuilder("getComponent")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassNames.context(), "context")
                .returns(screenSpec.getComponentSpec().getClassName())
                .addStatement("return ($T) $L.getSystemService($T.$L)", screenSpec.getComponentSpec().getClassName(), "context", ClassNames.mvpConfig(), CONFIG_DAGGERSERVICENAME)
                .build();

        MethodSpec createComponentMethod = MethodSpec.methodBuilder("createComponent")
                .addModifiers(Modifier.PUBLIC)
                .returns(Object.class)
                .addParameter(screenSpec.getComponentSpec().getParentTypeName(), "parentComponent")
                .addAnnotation(Override.class)
                .addCode("return $T.builder()\n\t" +
                        ".component(parentComponent)\n\t" +
                        ".module(new Module())\n\t" +
                        ".build();\n", screenSpec.getDaggerComponentTypeName())
                .build();

        TypeSpec.Builder builder = TypeSpec.classBuilder(screenSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ComponentFactory.class), screenSpec.getComponentSpec().getParentTypeName()))
                .addAnnotation(generatedAnnotationSpec)
                .addMethod(createComponentMethod)
                .addMethod(getComponentMethod);

        if (screenSpec.getSuperclassTypeName() != null) {
            builder.superclass(screenSpec.getSuperclassTypeName());
        }

        if (screenSpec.getAnnotationSpecs() != null) {
            for (ScreenAnnotationSpec annotationSpec : screenSpec.getAnnotationSpecs()) {
                AnnotationSpec.Builder annotationSpecBuilder = AnnotationSpec.builder(annotationSpec.getClassName());
                for (Map.Entry<String, Object> entry : annotationSpec.getMembers().entrySet()) {
                    annotationSpecBuilder.addMember(entry.getKey(), "$L", entry.getValue());
                }
                builder.addAnnotation(annotationSpecBuilder.build());
            }
        }

        List<VariableElement> constructorParameters = new ArrayList<>();
        for (InjectableVariableElement injectableVariableElement : screenSpec.getScreenParamMembers()) {
            builder.addField(FieldSpec.builder(TypeName.get(injectableVariableElement.getElement().asType()), injectableVariableElement.getElement().getSimpleName().toString())
                    .addModifiers(Modifier.PRIVATE)
                    .build());
            constructorParameters.add(injectableVariableElement.getElement());
        }

        if (!constructorParameters.isEmpty()) {
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC);
            for (VariableElement variableElement : constructorParameters) {
                constructorBuilder.addParameter(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString());
                constructorBuilder.addStatement("this.$L = $L", variableElement.getSimpleName().toString(), variableElement.getSimpleName().toString());
            }
            builder.addMethod(constructorBuilder.build());
        }

        return builder;
    }

    public TypeSpec compose(ConfigSpec configSpec) {
        AnnotationSpec generatedAnnotationSpec = AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", AnnotationProcessor.class.getName())
                .build();

        return TypeSpec.classBuilder(configSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(generatedAnnotationSpec)
                .addField(FieldSpec.builder(String.class, CONFIG_DAGGERSERVICENAME, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", configSpec.getDaggerServiceName())
                        .build())
                .build();
    }
}
