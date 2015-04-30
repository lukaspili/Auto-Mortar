package mvp.compiler.processingstep;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.lang.model.element.Element;

import mvp.compiler.extractor.ConfigurationExtractor;
import mvp.compiler.message.Message;
import mvp.compiler.message.MessageDelivery;
import mvp.compiler.model.Configuration;
import mvp.config.MvpConfiguration;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ConfigurationProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private final MessageDelivery messageDelivery;
    private final ProcessingStepsBus processingStepsBus;

    public ConfigurationProcessingStep(MessageDelivery messageDelivery, ProcessingStepsBus processingStepsBus) {
        this.messageDelivery = messageDelivery;
        this.processingStepsBus = processingStepsBus;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.<Class<? extends Annotation>>of(MvpConfiguration.class);
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        ConfigurationExtractor configurationExtractor = null;

        for (Class<? extends Annotation> annotation : elementsByAnnotation.keySet()) {
            Set<Element> elements = elementsByAnnotation.get(annotation);
            for (Element element : elements) {
                if (configurationExtractor != null) {
                    messageDelivery.add(Message.error(element, "Cannot have more than one @Config for Mortar-MVP"));
                    break;
                }

                configurationExtractor = new ConfigurationExtractor(element);
            }
        }

        if (configurationExtractor != null) {
            processingStepsBus.setConfiguration(Configuration.from(configurationExtractor));
        }
    }


}
