package mvp.compiler.extractor;

import com.google.common.base.Optional;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import mvp.navigation.MVP;

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
    private static final String MVP_PARENT_COMPONENT = "parentComponent";
    private static final String MVP_LAYOUT = "layout";
    private static final String MVP_SCREEN_SUPERCLASS = "screenSuperclass";

    private final Element element;
    private final TypeMirror viewTypeMirror;
    private final TypeMirror viewBaseLayoutTypeMirror;
    private final TypeMirror parentComponentTypeMirror;
    private final TypeMirror elementParameterizedType;
    private final TypeMirror screenSuperclassTypeMirror;
    private final int layout;
    private final MvpAnnotationSource mvpAnnotationSource;

    public ElementExtractor(Element element, Class<? extends Annotation> annotation, Types types, Elements elements) {
        this.element = element;

        viewTypeMirror = Utils.getValueFromAnnotation(element, annotation, MVP_VIEW);
        viewBaseLayoutTypeMirror = Utils.getValueFromAnnotation(element, annotation, MVP_VIEW_BASE_LAYOUT);
        parentComponentTypeMirror = Utils.getValueFromAnnotation(element, annotation, MVP_PARENT_COMPONENT);
        elementParameterizedType = extractParameterizedType(types, elements);

        if (annotation.equals(MVP.class)) {
            mvpAnnotationSource = MvpAnnotationSource.FLOW_NAVIGATION;
            screenSuperclassTypeMirror = null;
        } else if (annotation.equals(mvp.flowpath.MVP.class)) {
            mvpAnnotationSource = MvpAnnotationSource.FLOW_PATH;
            screenSuperclassTypeMirror = null;
        } else {
            mvpAnnotationSource = MvpAnnotationSource.STANDALONE;
            screenSuperclassTypeMirror = Utils.getValueFromAnnotation(element, annotation, MVP_SCREEN_SUPERCLASS);
        }

        Integer layout = Utils.getValueFromAnnotation(element, annotation, MVP_LAYOUT);
        this.layout = layout != null ? layout : 0;


    }

//    private int extractLayout(Class<? extends Annotation> annotation) {
//        if (mvpAnnotationSource == MvpAnnotationSource.FLOW_NAVIGATION) {
//            return element.getAnnotation(MVP.class).layout();
//        } else {
//            return element.getAnnotation(mvp.flowpath.MVP.class).layout();
//        }
//    }

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

    public TypeMirror getParentComponentTypeMirror() {
        return parentComponentTypeMirror;
    }

    public TypeMirror getElementParameterizedType() {
        return elementParameterizedType;
    }

    public int getLayout() {
        return layout;
    }

    public MvpAnnotationSource getMvpAnnotationSource() {
        return mvpAnnotationSource;
    }

    public TypeMirror getScreenSuperclassTypeMirror() {
        return screenSuperclassTypeMirror;
    }

    public enum MvpAnnotationSource {
        FLOW_PATH, FLOW_NAVIGATION, STANDALONE
    }
}
