package automortar.compiler;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import automortar.config.AutoMortarConfig;
import processorworkflow.AbstractExtractor;
import processorworkflow.Errors;
import processorworkflow.ExtractorUtils;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ConfigExtractor extends AbstractExtractor {

    private static final String CONFIG_SCREEN_SUPERCLASS = "screenSuperclass";

    private TypeMirror screenSuperclassTypeMirror;
    private String daggerServiceName;

    public ConfigExtractor(Element element, Types types, Elements elements, Errors errors) {
        super(element, types, elements, errors);

        extract();
    }

    @Override
    public void extract() {
        AutoMortarConfig autoMortarConfig = element.getAnnotation(AutoMortarConfig.class);
        daggerServiceName = autoMortarConfig.daggerServiceName();
        screenSuperclassTypeMirror = ExtractorUtils.getValueFromAnnotation(element, AutoMortarConfig.class, CONFIG_SCREEN_SUPERCLASS);
    }

    public TypeMirror getScreenSuperclassTypeMirror() {
        return screenSuperclassTypeMirror;
    }

    public String getDaggerServiceName() {
        return daggerServiceName;
    }
}
