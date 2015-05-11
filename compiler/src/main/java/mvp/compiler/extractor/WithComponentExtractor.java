package mvp.compiler.extractor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import mvp.WithComponent;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class WithComponentExtractor {

    private final Element element;
    private final List<TypeMirror> typeMirrors;

    public WithComponentExtractor(Element element) {
        this.element = element;

        typeMirrors = new ArrayList<>();
        List<AnnotationValue> values = Utils.getValueFromAnnotation(element, WithComponent.class, "value");
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
