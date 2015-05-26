package mvp.compiler.extractor;

import com.google.auto.common.MoreElements;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Scope;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.autodagger.AutoComponent;
import mvp.AutoScreen;
import mvp.compiler.message.Message;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ScreenExtractor {

    private static final String COMPONENT = "component";
    private static final String COMPONENT_DEPENDENCIES = "dependencies";
    private static final String COMPONENT_MODULES = "modules";
    private static final String SCREEN_ANNOTATIONS = "screenAnnotations";

    private final Element element;
    private final AnnotationMirror scopeAnnotationTypeMirror;
    private final List<TypeMirror> componentDependencies;
    private final List<TypeMirror> componentModules;
    private final List<AnnotationMirror> screenAnnotationsMirrors;

    private List<Message> messages = new ArrayList<>();
    private boolean errors;

    public ScreenExtractor(Element element, Class<? extends Annotation> annotation, Types types, Elements elements) {
        this.element = element;

        AnnotationMirror autoComponent = Utils.getValueFromAnnotation(element, AutoScreen.class, COMPONENT);
        componentDependencies = findTypeMirrors(autoComponent, COMPONENT_DEPENDENCIES);
        componentModules = findTypeMirrors(autoComponent, COMPONENT_MODULES);

        screenAnnotationsMirrors = findScreenAnnotations(types);

        scopeAnnotationTypeMirror = findScope();
    }

    private List<TypeMirror> findTypeMirrors(AnnotationMirror autoComponent, String name) {
        List<TypeMirror> typeMirrors = new ArrayList<>();
        List<AnnotationValue> values = Utils.getValueFromAnnotation(autoComponent, AutoComponent.class, name);
        if (values != null) {
            for (AnnotationValue value : values) {
                TypeMirror tm = (TypeMirror) value.getValue();
                typeMirrors.add(tm);
            }
        }

        return typeMirrors;
    }

    private List<AnnotationMirror> findScreenAnnotations(Types types) {
        List<AnnotationMirror> annotationMirrors = new ArrayList<>();
        List<AnnotationValue> screenAnnotations = Utils.getValueFromAnnotation(element, AutoScreen.class, SCREEN_ANNOTATIONS);
        if (screenAnnotations != null) {
            // for each annotations on element
            for (AnnotationMirror mirror : element.getAnnotationMirrors()) {
                // for each annotation decalred in @MVP screenAnnotations
                for (AnnotationValue value : screenAnnotations) {
                    TypeMirror tm = (TypeMirror) value.getValue();
                    boolean sameType = types.isSameType(mirror.getAnnotationType(), tm);
                    if (sameType) {
                        // if annotation is declared in @AutoScreen screenAnnotations, take it
                        annotationMirrors.add(mirror);
                        break;
                    }
                }
            }
        }

        return annotationMirrors;
    }

    /**
     * Find annotation that is itself annoted with @Scope
     * If there is one, it will be later applied on the generated component
     * Otherwise the component will be unscoped
     * Throw error if more than one scope annotation found
     */
    private AnnotationMirror findScope() {
        AnnotationMirror annotationTypeMirror = null;

        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            Element annotationElement = annotationMirror.getAnnotationType().asElement();
            if (MoreElements.isAnnotationPresent(annotationElement, Scope.class)) {
                // already found one scope
                if (annotationTypeMirror != null) {
                    errors = true;
                    messages.add(Message.error(element, "Several dagger scopes on same element are not allowed"));
                    continue;
                }

                annotationTypeMirror = annotationMirror;
            }
        }

        return annotationTypeMirror;
    }

    public Element getElement() {
        return element;
    }

    public AnnotationMirror getScopeAnnotationTypeMirror() {
        return scopeAnnotationTypeMirror;
    }

    public List<TypeMirror> getComponentDependencies() {
        return componentDependencies;
    }

    public List<TypeMirror> getComponentModules() {
        return componentModules;
    }

    public List<AnnotationMirror> getScreenAnnotationsMirrors() {
        return screenAnnotationsMirrors;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public boolean isErrors() {
        return errors;
    }
}
