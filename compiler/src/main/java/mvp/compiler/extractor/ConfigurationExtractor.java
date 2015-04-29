package mvp.compiler.extractor;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;

import mvp.config.MvpConfiguration;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ConfigurationExtractor {

    private static final String CONFIG_BUTTERKNIFE = "butterknife";
    private static final String CONFIG_SCREEN_SUPERCLASS = "screenSuperclass";
    private static final String CONFIG_DAGGER_SERVICE = "daggerServiceName";

    private final boolean butterknife;
    private final TypeName screenSuperclassTypeName;
    private final String daggerServiceName;

    public ConfigurationExtractor() throws Exception {
        System.out.println("NEW");
        butterknife = (boolean) MvpConfiguration.class.getMethod(CONFIG_BUTTERKNIFE).getDefaultValue();
        screenSuperclassTypeName = (Class) MvpConfiguration.class.getMethod(CONFIG_BUTTERKNIFE).getDefaultValue();
        daggerServiceName = (String) MvpConfiguration.class.getMethod(CONFIG_BUTTERKNIFE).getDefaultValue();
    }

    public ConfigurationExtractor(Element element) {
        MvpConfiguration mvpConfiguration = element.getAnnotation(MvpConfiguration.class);
        butterknife = mvpConfiguration.butterknife();
        daggerServiceName = mvpConfiguration.daggerServiceName();
        System.out.println("DONE2");
        screenSuperclassTypeName = Utils.getValueFromAnnotation(element, MvpConfiguration.class, CONFIG_SCREEN_SUPERCLASS);
        System.out.println("DONE");
    }

    public boolean isButterknife() {
        return butterknife;
    }

    public Class getScreenSuperclassTypeName() {
        return screenSuperclassTypeName;
    }

    public String getDaggerServiceName() {
        return daggerServiceName;
    }
}
