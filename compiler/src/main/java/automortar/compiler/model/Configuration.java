package automortar.compiler.model;

import com.squareup.javapoet.TypeName;

import automortar.compiler.extractor.ConfigurationExtractor;
import automortar.config.DefaultAutoMortarConfig;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class Configuration {

    private final boolean butterknife;
    private final TypeName screenSuperclassTypeName;
    private final String daggerServiceName;

    public static Configuration from(ConfigurationExtractor configurationExtractor) {
        return new Configuration(configurationExtractor);
    }

    public static Configuration defaultConfig() {
        return new Configuration();
    }

    private Configuration() {
        butterknife = false;
        screenSuperclassTypeName = null;
        daggerServiceName = DefaultAutoMortarConfig.DAGGER_SERVICE_NAME;
    }

    private Configuration(ConfigurationExtractor configurationExtractor) {
        butterknife = configurationExtractor.isButterknife();
        screenSuperclassTypeName = TypeName.get(configurationExtractor.getScreenSuperclassTypeMirror());
        daggerServiceName = configurationExtractor.getDaggerServiceName();
    }

    public boolean isButterknife() {
        return butterknife;
    }

    public TypeName getScreenSuperclassTypeName() {
        return screenSuperclassTypeName;
    }

    public String getDaggerServiceName() {
        return daggerServiceName;
    }
}
