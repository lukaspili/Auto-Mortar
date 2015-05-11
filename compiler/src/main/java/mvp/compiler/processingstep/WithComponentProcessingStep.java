package mvp.compiler.processingstep;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import mvp.WithComponent;
import mvp.compiler.extractor.WithComponentExtractor;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class WithComponentProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private final ProcessingStepsBus processingStepsBus;

    public WithComponentProcessingStep(ProcessingStepsBus processingStepsBus) {
        this.processingStepsBus = processingStepsBus;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.<Class<? extends Annotation>>of(WithComponent.class);
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        List<WithComponentExtractor> extractors = new ArrayList<>();

        for (Class<? extends Annotation> annotation : elementsByAnnotation.keySet()) {
            Set<Element> elements = elementsByAnnotation.get(annotation);
            for (Element element : elements) {
                WithComponentExtractor extractor = new WithComponentExtractor(element);
                extractors.add(extractor);
            }
        }

        processingStepsBus.setWithComponentExtractors(extractors);
    }


}
