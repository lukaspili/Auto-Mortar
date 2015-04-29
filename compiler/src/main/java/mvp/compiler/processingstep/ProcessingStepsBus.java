package mvp.compiler.processingstep;

import mvp.compiler.extractor.ConfigurationExtractor;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ProcessingStepsBus {

    private ConfigurationExtractor configurationExtractor;

    public ConfigurationExtractor getConfigurationExtractor() {
        return configurationExtractor;
    }

    public void setConfigurationExtractor(ConfigurationExtractor configurationExtractor) {
        this.configurationExtractor = configurationExtractor;
    }
}
