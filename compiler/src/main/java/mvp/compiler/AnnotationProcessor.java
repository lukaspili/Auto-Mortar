package mvp.compiler;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;

import javax.annotation.processing.Processor;
import javax.lang.model.SourceVersion;

import mvp.compiler.message.MessageDelivery;
import mvp.compiler.processingstep.ConfigurationProcessingStep;
import mvp.compiler.processingstep.MvpProcessingStep;
import mvp.compiler.processingstep.ProcessingStepsBus;
import mvp.compiler.processingstep.WithComponentProcessingStep;
import mvp.compiler.processingstep.WithInjectorProcessingStep;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends BasicAnnotationProcessor {

    private MessageDelivery messageDelivery = new MessageDelivery();
    private ProcessingStepsBus processingStepsBus = new ProcessingStepsBus();

    @Override
    protected Iterable<? extends ProcessingStep> initSteps() {
//        LinkedHashSet<ProcessingStep> steps = new LinkedHashSet<>();
////        steps.add(new WithComponentProcessingStep(processingStepsBus));
//        steps.add(new ConfigurationProcessingStep(messageDelivery, processingStepsBus));
//        steps.add(new WithInjectorProcessingStep(processingStepsBus));
//        steps.add(new MvpProcessingStep(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getFiler(), messageDelivery, processingStepsBus));
//        return steps;

        return ImmutableSet.of(
                new ConfigurationProcessingStep(messageDelivery, processingStepsBus),
                new WithInjectorProcessingStep(processingStepsBus),
                new WithComponentProcessingStep(processingStepsBus),
                new MvpProcessingStep(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getFiler(), messageDelivery, processingStepsBus)
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
