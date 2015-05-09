package mvp.compiler.processingstep;

import java.util.List;

import mvp.compiler.extractor.InjectableWithExtractor;
import mvp.compiler.model.Configuration;
import mvp.compiler.model.spec.InjectableWithSpec;

/**
 * Pass data between processing steps
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ProcessingStepsBus {

    private List<InjectableWithSpec> injectableWithSpecs;
    private List<InjectableWithExtractor> injectableWithExtractors;

    private Configuration configuration;
    private boolean configGenerated;

    public List<InjectableWithExtractor> getInjectableWithExtractors() {
        return injectableWithExtractors;
    }

    public void setInjectableWithExtractors(List<InjectableWithExtractor> injectableWithExtractors) {
        this.injectableWithExtractors = injectableWithExtractors;
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
