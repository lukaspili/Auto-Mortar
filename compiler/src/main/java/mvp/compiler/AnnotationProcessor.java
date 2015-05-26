package mvp.compiler;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;

import mvp.compiler.message.MessageDelivery;
import mvp.compiler.processingstep.ConfigurationProcessingStep;
import mvp.compiler.processingstep.ScreenProcessingStep;
import mvp.compiler.processingstep.ProcessingStepsBus;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends BasicAnnotationProcessor {

    private MessageDelivery messageDelivery = new MessageDelivery();
    private ProcessingStepsBus processingStepsBus = new ProcessingStepsBus();

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
        return ImmutableSet.of(
                new ConfigurationProcessingStep(messageDelivery, processingStepsBus),
                new ScreenProcessingStep(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getFiler(), messageDelivery, processingStepsBus)
        );
    }

    @Override
    protected void postProcess() {
        messageDelivery.deliver(processingEnv.getMessager());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
