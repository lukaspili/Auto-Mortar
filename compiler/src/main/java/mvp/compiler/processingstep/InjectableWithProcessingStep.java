package mvp.compiler.processingstep;

import com.google.auto.common.BasicAnnotationProcessor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

import mvp.InjectableWith;
import mvp.compiler.extractor.InjectableWithExtractor;
import mvp.compiler.message.MessageDelivery;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class InjectableWithProcessingStep implements BasicAnnotationProcessor.ProcessingStep {

    private final MessageDelivery messageDelivery;
    private final ProcessingStepsBus processingStepsBus;

    public InjectableWithProcessingStep(MessageDelivery messageDelivery, ProcessingStepsBus processingStepsBus) {
        this.messageDelivery = messageDelivery;
        this.processingStepsBus = processingStepsBus;
    }

    @Override
    public Set<? extends Class<? extends Annotation>> annotations() {
        return ImmutableSet.<Class<? extends Annotation>>of(InjectableWith.class);
    }

    @Override
    public void process(SetMultimap<Class<? extends Annotation>, Element> elementsByAnnotation) {
        List<InjectableWithExtractor> extractors = new ArrayList<>();

        for (Class<? extends Annotation> annotation : elementsByAnnotation.keySet()) {
            Set<Element> elements = elementsByAnnotation.get(annotation);
            for (Element element : elements) {
                InjectableWithExtractor extractor = new InjectableWithExtractor(element);

//                String pkg = MoreElements.getPackage(element).getQualifiedName().toString();
//                String name = element.getSimpleName().toString();
//                ClassName className = ClassName.get(pkg, name);
//
//                element.asType();
//
//                InjectableWithSpec spec = new InjectableWithSpec(className, ex);
                extractors.add(extractor);
            }
        }

        System.out.println("INJECTABLES " + extractors.size());

        processingStepsBus.setInjectableWithExtractors(extractors);
    }


}
