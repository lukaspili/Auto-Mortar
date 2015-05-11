package mvp.compiler.processingstep;

import java.util.List;

import mvp.compiler.extractor.WithInjectorExtractor;
import mvp.compiler.model.Configuration;

/**
 * Pass data between processing steps
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ProcessingStepsBus {

    private List<WithInjectorExtractor> withInjectorExtractors;
    private Configuration configuration;
    private boolean configGenerated;

    public List<WithInjectorExtractor> getWithInjectorExtractors() {
        return withInjectorExtractors;
    }

    public void setWithInjectorExtractors(List<WithInjectorExtractor> withInjectorExtractors) {
        this.withInjectorExtractors = withInjectorExtractors;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public boolean isConfigGenerated() {
        return configGenerated;
    }

    public void setConfigGenerated(boolean configGenerated) {
        this.configGenerated = configGenerated;
    }
}
