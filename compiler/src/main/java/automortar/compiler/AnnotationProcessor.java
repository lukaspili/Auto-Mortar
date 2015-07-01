package automortar.compiler;

import com.google.auto.service.AutoService;

import java.util.LinkedList;

import javax.annotation.processing.Processor;

import processorworkflow.AbstractProcessing;
import processorworkflow.AbstractProcessor;
import processorworkflow.Logger;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor<State> {

    public AnnotationProcessor() {
        Logger.init("Auto Mortar Processor", true);
    }

    @Override
    protected State processingState() {
        return new State();
    }

    @Override
    protected LinkedList<AbstractProcessing> processings() {
        LinkedList<AbstractProcessing> processings = new LinkedList<>();
        processings.add(new ConfigProcessing(elements, types, errors, state));
        processings.add(new ScreenProcessing(elements, types, errors, state));
        return processings;
    }


    //    private MessageDelivery messageDelivery = new MessageDelivery();
//    private State state = new State();
//    private Set<ProcessingStep> processingSteps;
//
//    private boolean stop;
//
//    @Override
//    public synchronized void init(ProcessingEnvironment processingEnv) {
//        super.init(processingEnv);
//
//        processingSteps = new LinkedHashSet<>();
//        processingSteps.add(new ConfigurationProcessingStep(messageDelivery, state));
//        processingSteps.add(new ScreenProcessingStep(processingEnv.getTypeUtils(), processingEnv.getElementUtils(), processingEnv.getFiler(), messageDelivery, state));
//    }
//
//    @Override
//    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        if (stop) return false;
//
//        for (ProcessingStep processingStep : processingSteps) {
//            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(processingStep.annotation());
//            processingStep.process(elements);
//        }
//
//        messageDelivery.deliver(processingEnv.getMessager());
//        if (messageDelivery.hasErrors()) {
//            stop = true;
//        }
//
//        return false;
//    }
//
//    @Override
//    public Set<String> getSupportedAnnotationTypes() {
//        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
//        for (ProcessingStep step : processingSteps) {
//            builder.add(step.annotation().getName());
//        }
//        return builder.build();
//    }
//
//    @Override
//    public SourceVersion getSupportedSourceVersion() {
//        return SourceVersion.latestSupported();
//    }
}
