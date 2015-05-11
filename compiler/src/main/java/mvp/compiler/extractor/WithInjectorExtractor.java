package mvp.compiler.extractor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import mvp.WithInjector;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class WithInjectorExtractor {

    private final Element element;
    private final List<TypeMirror> typeMirrors;

    public WithInjectorExtractor(Element element) {
        this.element = element;

        typeMirrors = new ArrayList<>();
        List<AnnotationValue> values = Utils.getValueFromAnnotation(element, WithInjector.class, "value");
        if (values != null) {
            for (AnnotationValue value : values) {
                TypeMirror tm = (TypeMirror) value.getValue();
                typeMirrors.add(tm);
            }
        }
    }

    public Element getElement() {
        return element;
    }

    public List<TypeMirror> getTypeMirrors() {
        return typeMirrors;
    }
}
