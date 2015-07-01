package automortar.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import automortar.ScreenComponentFactory;
import dagger.Module;
import dagger.Provides;
import processorworkflow.AbstractComposer;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ScreenComposer extends AbstractComposer<ScreenSpec> {

    private static final ClassName CONTEXT_CLS = ClassName.get("android.content", "Context");

    public ScreenComposer(List<ScreenSpec> specs) {
        super(specs);
    }

    @Override
    protected JavaFile compose(ScreenSpec spec) {
        TypeSpec typeSpec = build(spec);
        return JavaFile.builder(spec.getClassName().packageName(), typeSpec).build();
    }

    private TypeSpec build(ScreenSpec spec) {

        MethodSpec createComponentMethodSpec = buildCreateComponent(spec);

        MethodSpec getComponentMethodSpec = MethodSpec.methodBuilder("getComponent")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(CONTEXT_CLS, "context")
                .returns(spec.getComponentClassName())
                .addStatement("return ($T) $L.getSystemService($T.$L)", spec.getComponentClassName(), "context", ConfigComposer.CLS, ConfigComposer.DAGGER_SERVICE_NAME)
                .build();

        List<FieldSpec> fieldSpecs = new ArrayList<>();
        for (ParameterSpec parameterSpec : spec.getModuleSpec().getInternalParameters()) {
            fieldSpecs.add(FieldSpec.builder(parameterSpec.type, parameterSpec.name)
                    .addModifiers(Modifier.PRIVATE)
                    .build());
        }

        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameters(spec.getModuleSpec().getInternalParameters());
        for (ParameterSpec parameterSpec : spec.getModuleSpec().getInternalParameters()) {
            constructorBuilder.addStatement("this.$L = $L", parameterSpec.name, parameterSpec.name);
        }

        TypeSpec.Builder builder = TypeSpec.classBuilder(spec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ScreenComponentFactory.class), spec.getDaggerComponentBuilderDependencyTypeName()))
                .addAnnotation(AnnotationSpec.builder(Generated.class).addMember("value", "$S", AnnotationProcessor.class.getName()).build())
                .addAnnotation(spec.getComponentAnnotationSpec())
                .addType(buildModule(spec.getModuleSpec()))
                .addMethod(constructorBuilder.build())
                .addMethod(createComponentMethodSpec)
                .addMethod(getComponentMethodSpec)
                .addFields(fieldSpecs);

        if (spec.getConfigSpec().getScreenSuperclassTypeName() != null) {
            builder.superclass(spec.getConfigSpec().getScreenSuperclassTypeName());
        }

        if (spec.getScopeAnnotationSpec() != null) {
            builder.addAnnotation(spec.getScopeAnnotationSpec());
        }

        if (!spec.getScreenAnnotationSpecs().isEmpty()) {
            builder.addAnnotations(spec.getScreenAnnotationSpecs());
        }

        return builder.build();
    }

    private TypeSpec buildModule(ModuleSpec spec) {
        CodeBlock.Builder blockBuilder = CodeBlock.builder().add("return new $T(", spec.getPresenterTypeName());
        int i = 0;
        for (ParameterSpec parameterSpec : spec.getPresenterArgs()) {
            blockBuilder.add(parameterSpec.name);

            if (i++ < spec.getPresenterArgs().size() - 1) {
                blockBuilder.add(", ");
            }
        }
        blockBuilder.add(");\n");

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("providesPresenter")
                .addModifiers(Modifier.PUBLIC)
                .returns(spec.getPresenterTypeName())
                .addAnnotation(Provides.class)
                .addParameters(spec.getProvideParameters())
                .addCode(blockBuilder.build());

        if (spec.getScopeAnnotationSpec() != null) {
            methodSpecBuilder.addAnnotation(spec.getScopeAnnotationSpec());
        }

        return TypeSpec.classBuilder(spec.getClassName().simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Module.class)
                .addMethod(methodSpecBuilder.build())
                .build();
    }

    private MethodSpec buildCreateComponent(ScreenSpec screenSpec) {

        MethodSpec servicesMethodSpec = MethodSpec.methodBuilder("createComponent")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(Object.class)
                .addParameter(screenSpec.getDaggerComponentBuilderDependencyTypeName(), "dependency")
                .addCode(CodeBlock.builder()
                        .add("return $T.builder()\n", screenSpec.getDaggerComponentTypeName())
                        .indent()
                        .add(".$L(dependency)\n", screenSpec.getDaggerComponentBuilderDependencyMethodName())
                        .add(".module(new Module())\n")
                        .add(".build();\n")
                        .unindent()
                        .build())
                .build();

        return servicesMethodSpec;


//        List<TypeName> params = new ArrayList<>();
//
//        // first param of "return $T.Builer"
//        params.add(ClassNames.daggerComponent(screenSpec.getComponentClassName()));
//
//        // build dependencies setters
//        StringBuilder dependenciesBuilder = new StringBuilder();
//        int i = 0;
//        for (AutoComponentMemberSpec spec : screenSpec.getComponentDependenciesSpecs()) {
//            dependenciesBuilder.append(".")
//                    .append(spec.getName())
//                    .append("(($T)dependencies[")
//                    .append(i)
//                    .append("])")
//                    .append("\n\t");
//            params.add(spec.getRealTypeName());
//        }
//
//        // build modules setters
//        StringBuilder modulesBuilder = new StringBuilder();
//        for (AutoComponentMemberSpec spec : screenSpec.getComponentModulesSpecs()) {
//            modulesBuilder.append(".")
//                    .append(spec.getName())
//                    .append("(new $T())")
//                    .append("\n\t");
//            params.add(spec.getTypeName());
//        }
//
//        MethodSpec.Builder builder = MethodSpec.methodBuilder("createComponent")
//                .addModifiers(Modifier.PUBLIC)
//                .returns(Object.class)
//                .addParameter(Object.class, "dependency")
//                .addAnnotation(Override.class)
//                .addCode("return $T.builder()\n\t" +
//                        dependenciesBuilder.toString() +
//                        modulesBuilder.toString() +
//                        ".build();\n", params.toArray());
//
//
//        return builder.build();
    }
}
