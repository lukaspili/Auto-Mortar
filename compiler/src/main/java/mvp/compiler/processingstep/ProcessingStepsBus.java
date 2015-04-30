package mvp.compiler.processingstep;

import mvp.compiler.model.Configuration;

/**
 * Pass data between processing steps
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ProcessingStepsBus {

    private Configuration configuration;

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
