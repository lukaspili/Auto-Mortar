package automortar.compiler;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import automortar.compiler.message.MessageDelivery;
import automortar.compiler.processingstep.ConfigurationProcessingStep;
import automortar.compiler.processingstep.ProcessingStep;
import automortar.compiler.processingstep.ProcessingStepsBus;
import automortar.compiler.processingstep.ScreenProcessingStep;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {

    private MessageDelivery messageDelivery = new MessageDelivery();
    private ProcessingStepsBus processingStepsBus = new ProcessingStepsBus();
    private Set<ProcessingStep> processingSteps;

    private boolean stop;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        processingSteps = new LinkedHashSet<>();
        processingSteps.add(new ConfigurationProcessingStep(messageDelivery, processingStepsBus));
        processingSteps.add(new ScreenProcessingStep(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getFiler(), messageDelivery, processingStepsBus));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (stop) return false;

        for (ProcessingStep processingStep : processingSteps) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(processingStep.annotation());
            processingStep.process(elements);
        }

        messageDelivery.deliver(processingEnv.getMessager());
        if (messageDelivery.hasErrors()) {
            stop = true;
        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (ProcessingStep step : processingSteps) {
            builder.add(step.annotation().getName());
        }
        return builder.build();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
