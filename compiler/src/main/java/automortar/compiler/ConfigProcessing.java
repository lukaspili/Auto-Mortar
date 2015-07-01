package automortar.compiler;

import com.google.common.collect.ImmutableSet;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import automortar.config.AutoMortarConfig;
import automortar.config.DefaultAutoMortarConfig;
import processorworkflow.AbstractComposer;
import processorworkflow.AbstractProcessing;
import processorworkflow.Errors;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ConfigProcessing extends AbstractProcessing<ConfigSpec, State> {

    private ConfigExtractor configExtractor;

    public ConfigProcessing(Elements elements, Types types, Errors errors, State state) {
        super(elements, types, errors, state);
    }

    @Override
    public Set<Class<? extends Annotation>> supportedAnnotations() {
        Set set = ImmutableSet.of(AutoMortarConfig.class);
        return set;
    }

    @Override
    protected void processElements(Set<? extends Element> annotationElements) {
        super.processElements(annotationElements);

        if (errors.hasErrors()) {
            return;
        }

        ConfigSpec spec = new ConfigSpec();
        if (configExtractor != null) {
            spec.setDaggerServiceName(configExtractor.getDaggerServiceName());
            if (configExtractor.getScreenSuperclassTypeMirror() != null) {
                spec.setScreenSuperclassTypeName(TypeName.get(configExtractor.getScreenSuperclassTypeMirror()));
            }
        } else {
            spec.setDaggerServiceName(DefaultAutoMortarConfig.DAGGER_SERVICE_NAME);
        }

        state.setConfigSpec(spec);
        specs.add(spec);
    }

    @Override
    public boolean processElement(Element element, Errors.ElementErrors elementErrors) {
        if (configExtractor != null) {
            elementErrors.addInvalid("Multiple configurations are forbidden");
            return false;
        }

        configExtractor = new ConfigExtractor(element, types, elements, errors);
        if (errors.hasErrors()) {
            return false;
        }

        return true;
    }

    @Override
    public AbstractComposer<ConfigSpec> createComposer() {
        return new ConfigComposer(specs);
    }
}
