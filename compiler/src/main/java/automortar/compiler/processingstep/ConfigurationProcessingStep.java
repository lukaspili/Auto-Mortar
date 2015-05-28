package automortar.compiler.processingstep;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;

import automortar.compiler.extractor.ConfigurationExtractor;
import automortar.compiler.message.Message;
import automortar.compiler.message.MessageDelivery;
import automortar.compiler.model.Configuration;
import automortar.config.AutoMortarConfig;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ConfigurationProcessingStep implements ProcessingStep {

    private final MessageDelivery messageDelivery;
    private final ProcessingStepsBus processingStepsBus;

    public ConfigurationProcessingStep(MessageDelivery messageDelivery, ProcessingStepsBus processingStepsBus) {
        this.messageDelivery = messageDelivery;
        this.processingStepsBus = processingStepsBus;
    }

    @Override
    public Class<? extends Annotation> annotation() {
        return AutoMortarConfig.class;
    }

    @Override
    public void process(Set<? extends Element> elements) {
        ConfigurationExtractor configurationExtractor = null;

        for (Element element : elements) {
            if (configurationExtractor != null) {
                messageDelivery.add(Message.error(element, "Multiple configurations are forbidden"));
                break;
            }

            configurationExtractor = new ConfigurationExtractor(element);
        }

        if (configurationExtractor != null) {
            processingStepsBus.setConfiguration(Configuration.from(configurationExtractor));
        }
    }


}
