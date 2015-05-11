package mvp.compiler.processingstep;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import mvp.WithInjector;
import mvp.compiler.extractor.WithInjectorExtractor;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class WithInjectorProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private final ProcessingStepsBus processingStepsBus;

    public WithInjectorProcessingStep(ProcessingStepsBus processingStepsBus) {
        this.processingStepsBus = processingStepsBus;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.<Class<? extends Annotation>>of(WithInjector.class);
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        List<WithInjectorExtractor> extractors = new ArrayList<>();

        for (Class<? extends Annotation> annotation : elementsByAnnotation.keySet()) {
            Set<Element> elements = elementsByAnnotation.get(annotation);
            for (Element element : elements) {
                WithInjectorExtractor extractor = new WithInjectorExtractor(element);
                extractors.add(extractor);
            }
        }

        processingStepsBus.setWithInjectorExtractors(extractors);
    }


}
