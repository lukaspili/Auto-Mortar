package mvp.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
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
import dagger.Module;
import dagger.Provides;
import mvp.ScreenComponentFactory;
import mvp.compiler.model.InjectableVariableElement;
import mvp.compiler.model.spec.AutoComponentMemberSpec;
import mvp.compiler.model.spec.ConfigSpec;
import mvp.compiler.model.spec.ModuleSpec;
import mvp.compiler.model.spec.ScreenAnnotationSpec;
import mvp.compiler.model.spec.ScreenSpec;
import mvp.compiler.names.ClassNames;

/**
 * Actually it generates readable and understandable code!
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class MisunderstoodPoet {

    private final static String CONFIG_DAGGERSERVICENAME = "DAGGER_SERVICE_NAME";

    public TypeSpec compose(ScreenSpec screenSpec) {
        System.out.println("YO COMPOSE");
        TypeSpec.Builder screenTypeSpecBuilder = createScreenBuilder(screenSpec);
        System.out.println("SCREEN DONE");
        screenTypeSpecBuilder.addType(composeModule(screenSpec.getModuleSpec()));
        System.out.println("MODULE DONE");

        System.out.println("DONE COMPOSE");

        // and compose!
        return screenTypeSpecBuilder.build();
    }

    public TypeSpec composeModule(ModuleSpec moduleSpec) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("providePresenter")
                .addModifiers(Modifier.PUBLIC)
                .returns(moduleSpec.getPresenterClassName())
                .addAnnotation(Provides.class);

        // Scope annotation if any
        if (moduleSpec.getScreenSpec().getScopeAnnotationMirror() != null) {
            builder.addAnnotation(AnnotationSpec.get(moduleSpec.getScreenSpec().getScopeAnnotationMirror()));
        }

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
        AnnotationSpec autoComponentAnnotationSpec = buildAutoComponentAnnotationSpec(screenSpec);

        // static getComponent(Context)
        MethodSpec getComponentMethod = MethodSpec.methodBuilder("getComponent")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassNames.context(), "context")
                .returns(screenSpec.getComponentClassName())
                .addStatement("return ($T) $L.getSystemService($T.$L)", screenSpec.getComponentClassName(), "context", ClassNames.mvpConfig(), CONFIG_DAGGERSERVICENAME)
                .build();

        System.out.println("YO 1");

        // createComponent()
        MethodSpec createComponentMethod = buildScreenCreateComponentMethodSpec(screenSpec);

        TypeSpec.Builder builder = TypeSpec.classBuilder(screenSpec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ClassName.get(ScreenComponentFactory.class))
                .addAnnotation(generatedAnnotationSpec)
                .addAnnotation(autoComponentAnnotationSpec)
                .addMethod(createComponentMethod)
                .addMethod(getComponentMethod);

        // Superclass if provided
        if (screenSpec.getSuperclassTypeName() != null) {
            builder.superclass(screenSpec.getSuperclassTypeName());
        }

        // Scope annotation if provided
        if (screenSpec.getScopeAnnotationMirror() != null) {
            builder.addAnnotation(AnnotationSpec.get(screenSpec.getScopeAnnotationMirror()));
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

    private MethodSpec buildScreenCreateComponentMethodSpec(ScreenSpec screenSpec) {

        List<TypeName> params = new ArrayList<>();

        // first param of "return $T.Builer"
        params.add(ClassNames.daggerComponent(screenSpec.getComponentClassName()));

        // build dependencies setters
        StringBuilder dependenciesBuilder = new StringBuilder();
        int i = 0;
        for (AutoComponentMemberSpec spec : screenSpec.getComponentDependenciesSpecs()) {
            dependenciesBuilder.append(".")
                    .append(spec.getName())
                    .append("(($T)dependencies[")
                    .append(i)
                    .append("])")
                    .append("\n\t");
            params.add(spec.getRealTypeName());
        }

        // build modules setters
        StringBuilder modulesBuilder = new StringBuilder();
        for (AutoComponentMemberSpec spec : screenSpec.getComponentModulesSpecs()) {
            modulesBuilder.append(".")
                    .append(spec.getName())
                    .append("(new $T())")
                    .append("\n\t");
            params.add(spec.getTypeName());
        }

        MethodSpec.Builder builder = MethodSpec.methodBuilder("createComponent")
                .addModifiers(Modifier.PUBLIC)
                .returns(Object.class)
                .addParameter(TypeName.get(Object[].class), "dependencies")
                .varargs()
                .addAnnotation(Override.class)
                .addCode("return $T.builder()\n\t" +
                        dependenciesBuilder.toString() +
                        modulesBuilder.toString() +
                        ".build();\n", params.toArray());


        return builder.build();
    }

    private AnnotationSpec buildAutoComponentAnnotationSpec(ScreenSpec screenSpec) {
        // modules are never empty
        AnnotationSpec.Builder builder = AnnotationSpec.builder(AutoComponent.class)
                .addMember("modules", PoetUtils.getStringOfClassArrayTypes(screenSpec.getComponentModulesSpecs().size()), SpecUtils.getTypeNames(screenSpec.getComponentModulesSpecs()));

        // dependencies
        if (!screenSpec.getComponentDependenciesSpecs().isEmpty()) {
            builder.addMember("dependencies", PoetUtils.getStringOfClassArrayTypes(screenSpec.getComponentDependenciesSpecs().size()), SpecUtils.getTypeNames(screenSpec.getComponentDependenciesSpecs()));
        }

        return builder.build();
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
