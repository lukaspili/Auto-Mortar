package mvp.compiler.extractor;

import com.google.common.base.Optional;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Extract and holds the useful data from Element
 * It allows to not repeat extraction in different phases of annotation processing
 * It does not perform any validation, so any property may be null
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ElementExtractor {

    private static final String MVP_VIEW_BASE_LAYOUT = "baseViewLayout";
    private static final String MVP_VIEW = "view";
    private static final String MVP_PARENT = "parent";
    private static final String MVP_SCREEN_SUPERCLASS = "screenSuperclass";
    private static final String MVP_SCREEN_ANNOTATIONS = "screenAnnotations";

    private final Element element;
    private final TypeMirror viewTypeMirror;
    private final TypeMirror viewBaseLayoutTypeMirror;
    private final TypeMirror parentTypeMirror;
    private final TypeMirror elementParameterizedType;
    private final TypeMirror screenSuperclassTypeMirror;
    private final List<AnnotationMirror> screenAnnotationsMirrors;

    public ElementExtractor(Element element, Class<? extends Annotation> annotation, Types types, Elements elements) {
        this.element = element;

        viewTypeMirror = Utils.getValueFromAnnotation(element, annotation, MVP_VIEW);
        viewBaseLayoutTypeMirror = Utils.getValueFromAnnotation(element, annotation, MVP_VIEW_BASE_LAYOUT);
        parentTypeMirror = Utils.getValueFromAnnotation(element, annotation, MVP_PARENT);
        elementParameterizedType = extractParameterizedType(types, elements);
        screenSuperclassTypeMirror = Utils.getValueFromAnnotation(element, annotation, MVP_SCREEN_SUPERCLASS);

        screenAnnotationsMirrors = new ArrayList<>();
        List<AnnotationValue> screenAnnotations = Utils.getValueFromAnnotation(element, annotation, MVP_SCREEN_ANNOTATIONS);
        if (screenAnnotations != null) {
            // for each annotations on element
            for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
                // for each annotation decalred in @MVP screenAnnotations
                for (AnnotationValue value : screenAnnotations) {
                    TypeMirror tm = (TypeMirror) value.getValue();
                    boolean sameType = types.isSameType(mirror.getAnnotationType(), tm);
                    if (sameType) {
                        // if annotation is declared in @MVP screenAnnotations, take it
                        screenAnnotationsMirrors.add(mirror);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Iterate over @MVP annotated presenter superclasses until it finds the ViewPresenter<?> parameterized type
     * <p/>
     * Current limitation is that it does not support fully multiple parameterized types
     * Each superclass must declare the presenter associated view as its first parameterized type
     *
     * @return the TypeMirror of the ViewPresenter<?> parameterized type or null if none
     */
    private TypeMirror extractParameterizedType(Types types, Elements elements) {
        Element superclassElement = element;

        Optional<DeclaredType> declaredTypeResult;
        while ((declaredTypeResult = Utils.getSuperclassDeclaredType(types, elements, superclassElement)).isPresent()) {
            superclassElement = declaredTypeResult.get().asElement();

            if (!declaredTypeResult.get().getTypeArguments().isEmpty()) {
                // take the first parameterized type to associate to the view
                return declaredTypeResult.get().getTypeArguments().get(0);
            }
        }

        return null;
    }

    public Element getElement() {
        return element;
    }

    public TypeMirror getViewTypeMirror() {
        return viewTypeMirror;
    }

    public TypeMirror getViewBaseLayoutTypeMirror() {
        return viewBaseLayoutTypeMirror;
    }

    public TypeMirror getParentTypeMirror() {
        return parentTypeMirror;
    }

    public TypeMirror getElementParameterizedType() {
        return elementParameterizedType;
    }

    public TypeMirror getScreenSuperclassTypeMirror() {
        return screenSuperclassTypeMirror;
    }

    public List<AnnotationMirror> getScreenAnnotationsMirrors() {
        return screenAnnotationsMirrors;
    }
}
