package automortar.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;

import processorworkflow.AbstractComposer;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ConfigComposer extends AbstractComposer<ConfigSpec> {

    public final static String DAGGER_SERVICE_NAME = "DAGGER_SERVICE_NAME";
    public final static ClassName CLS = ClassName.get("automortarconfig", "AutoMortarConfig");

    public ConfigComposer(List<ConfigSpec> specs) {
        super(specs);
    }

    @Override
    protected JavaFile compose(ConfigSpec spec) {
        TypeSpec typeSpec = build(spec);
        return JavaFile.builder(CLS.packageName(), typeSpec).build();
    }

    private TypeSpec build(ConfigSpec spec) {
        return TypeSpec.classBuilder(CLS.simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(Generated.class).addMember("value", "$S", AnnotationProcessor.class.getName()).build())
                .addField(FieldSpec.builder(String.class, DAGGER_SERVICE_NAME, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                        .initializer("$S", spec.getDaggerServiceName())
                        .build())
                .build();
    }
}
