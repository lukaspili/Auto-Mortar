package mvp.compiler.extractor;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import mvp.config.AutoMortarConfig;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ConfigurationExtractor {

    private static final String CONFIG_SCREEN_SUPERCLASS = "screenSuperclass";

    private final boolean butterknife;
    private final TypeMirror screenSuperclassTypeMirror;
    private final String daggerServiceName;

    public ConfigurationExtractor(Element element) {
        AutoMortarConfig autoMortarConfig = element.getAnnotation(AutoMortarConfig.class);
        butterknife = autoMortarConfig.butterknife();
        daggerServiceName = autoMortarConfig.daggerServiceName();
        screenSuperclassTypeMirror = Utils.getValueFromAnnotation(element, AutoMortarConfig.class, CONFIG_SCREEN_SUPERCLASS);
    }

    public boolean isButterknife() {
        return butterknife;
    }

    public TypeMirror getScreenSuperclassTypeMirror() {
        return screenSuperclassTypeMirror;
    }

    public String getDaggerServiceName() {
        return daggerServiceName;
    }
}
