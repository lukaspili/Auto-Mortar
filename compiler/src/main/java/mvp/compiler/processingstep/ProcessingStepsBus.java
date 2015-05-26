package mvp.compiler.processingstep;

import mvp.compiler.model.Configuration;

/**
 * Pass data between processing steps
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ProcessingStepsBus {

//    private List<WithInjectorExtractor> withInjectorExtractors;
//    private List<WithComponentExtractor> withComponentExtractors;
    private Configuration configuration;
    private boolean configGenerated;

//    public List<WithInjectorExtractor> getWithInjectorExtractors() {
//        return withInjectorExtractors;
//    }
//
//    public void setWithInjectorExtractors(List<WithInjectorExtractor> withInjectorExtractors) {
//        this.withInjectorExtractors = withInjectorExtractors;
//    }
//
//    public List<WithComponentExtractor> getWithComponentExtractors() {
//        return withComponentExtractors;
//    }
//
//    public void setWithComponentExtractors(List<WithComponentExtractor> withComponentExtractors) {
//        this.withComponentExtractors = withComponentExtractors;
//    }

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
