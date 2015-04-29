package mvp.compiler.processingstep;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import mvp.compiler.extractor.ConfigurationExtractor;
import mvp.compiler.message.Message;
import mvp.compiler.message.MessageDelivery;
import mvp.config.MvpConfiguration;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ConfigurationProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private final Filer filer;
    private final MessageDelivery messageDelivery;
    private final ProcessingStepsBus processingStepsBus;

    public ConfigurationProcessingStep(Filer filer, MessageDelivery messageDelivery, ProcessingStepsBus processingStepsBus) {
        this.filer = filer;
        this.messageDelivery = messageDelivery;
        this.processingStepsBus = processingStepsBus;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.<Class<? extends Annotation>>of(MvpConfiguration.class);
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        for (Class<? extends Annotation> annotation : elementsByAnnotation.keySet()) {
            Set<Element> elements = elementsByAnnotation.get(annotation);
            for (Element element : elements) {
                if (processingStepsBus.getConfigurationExtractor() != null) {
                    messageDelivery.add(Message.error(element, "Cannot have more than one @Config for Mortar-MVP"));
                    break;
                }

                ConfigurationExtractor configurationExtractor = new ConfigurationExtractor(element);
                processingStepsBus.setConfigurationExtractor(configurationExtractor);

                System.out.println("YO");
            }
        }
    }
}
