package mvp.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import autodagger.autodagger.AutoComponent;
import autodagger.autodagger.AutoExpose;
import dagger.Module;
import dagger.Provides;
import mvp.compiler.model.InjectableVariableElement;
import mvp.compiler.model.spec.ConfigSpec;
import mvp.compiler.model.spec.ModuleSpec;
import mvp.compiler.model.spec.ScreenAnnotationSpec;
import mvp.compiler.model.spec.ScreenSpec;

/**
 * Actually it generates readable and understandable code!
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class MisunderstoodPoet {

    private final static String CONFIG_DAGGERSERVICENAME = "DAGGER_SERVICE_NAME";

    public TypeSpec compose(ScreenSpec screenSpec) {
        TypeSpec.Builder screenTypeSpecBuilder = createScreenBuilder(screenSpec);

        screenTypeSpecBuilder.addType(composeModule(screenSpec.getModuleSpec()));

        // and compose!
        return screenTypeSpecBuilder.build();
    }

    public TypeSpec composeModule(ModuleSpec moduleSpec) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("providePresenter")
                .addModifiers(Modifier.PUBLIC)
                .returns(moduleSpec.getPresenterClassName())
                .addAnnotation(Provides.class);

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

    private TypeSpec.Builder createScreenBuilder(ScreenSpec screenSpec) {
        // @Generated
        AnnotationSpec generatedAnnotationSpec = AnnotationSpec.builder(Generated.class)
                .addMember("value", "$S", AnnotationProcessor.class.getName())
                .build();

        // @AutoComponent
        AnnotationSpec autoComponentAnnotationSpec = AnnotationSpec.builder(AutoComponent.class)
                .build();

        // static getComponent(Context)
//        MethodSpec getComponentMethod = MethodSpec.methodBuilder("getComponent")
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .addParameter(ClassNames.context(), "context")
//                .returns(screenSpec.getComponentSpec().getClassName())
//                .addStatement("return ($T) $L.getSystemService($T.$L)", screenSpec.getComponentSpec().getClassName(), "context", ClassNames.mvpConfig(), CONFIG_DAGGERSERVICENAME)
//                .build();
//
//        MethodSpec createComponentMethod = MethodSpec.methodBuilder("createComponent")
//                .addModifiers(Modifier.PUBLIC)
//                .returns(Object.class)
//                .addParameter(screenSpec.getComponentSpec().getParentTypeName(), "parentComponent")
//                .addAnnotation(Override.class)
//                .addCode("return $T.builder()\n\t" +
//                        ".component(parentComponent)\n\t" +
//                        ".module(new Module())\n\t" +
//                        ".build();\n", screenSpec.getDaggerComponentTypeName())
//                .build();

        TypeSpec.Builder builder = TypeSpec.classBuilder(screenSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
//                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ComponentFactory.class), screenSpec.getComponentSpec().getParentTypeName()))
                .addAnnotation(generatedAnnotationSpec)
                .addAnnotation(autoComponentAnnotationSpec);
//                .addAnnotation(autoInjectorAnnotationSpec);
//                .addMethod(createComponentMethod)
//                .addMethod(getComponentMethod);

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
