package automortar.compiler;

import com.google.auto.common.MoreElements;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Scope;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import autodagger.compiler.utils.AutoComponentExtractorUtil;
import automortar.AutoScreen;
import processorworkflow.AbstractExtractor;
import processorworkflow.Errors;
import processorworkflow.ExtractorUtils;
import processorworkflow.Logger;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ScreenExtractor extends AbstractExtractor {

    private static final String COMPONENT = "component";
    private static final String SCREEN_ANNOTATIONS = "screenAnnotations";

    private TypeMirror componentDependency;
    private AnnotationMirror scopeAnnotationTypeMirror;
    private AnnotationMirror componentAnnotationTypeMirror;
    private List<VariableElement> constructorsParamtersElements;
    private List<AnnotationMirror> screenAnnotationsMirrors;

    public ScreenExtractor(Element element, Types types, Elements elements, Errors errors) {
        super(element, types, elements, errors);

        Logger.d("Extract %s", element.getSimpleName());
        extract();
    }

    @Override
    public void extract() {
        componentAnnotationTypeMirror = ExtractorUtils.getValueFromAnnotation(element, AutoScreen.class, COMPONENT);
        if (componentAnnotationTypeMirror == null) {
            errors.addMissing("@AutoComponent");
            return;
        }

        // get dependency from @AutoComponent
        List<TypeMirror> deps = AutoComponentExtractorUtil.getDependencies(componentAnnotationTypeMirror, errors);
        if (deps.size() != 1) {
            errors.addInvalid("@AutoComponent must have only 1 dependency");
            return;
        }
        componentDependency = deps.get(0);

        scopeAnnotationTypeMirror = findScope();
        screenAnnotationsMirrors = findScreenAnnotations();

        constructorsParamtersElements = new ArrayList<>();
        int constructorsCount = 0;
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
                constructorsCount++;
                for (VariableElement variableElement : MoreElements.asExecutable(enclosedElement).getParameters()) {
                    constructorsParamtersElements.add(variableElement);
                }
            }
        }

        if (constructorsCount > 1) {
            errors.addInvalid("Cannot have several constructors");
            return;
        }
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
                    errors.addInvalid("Several dagger scopes on same element are not allowed");
                    continue;
                }

                annotationTypeMirror = annotationMirror;
            }
        }

        return annotationTypeMirror;
    }

    private List<AnnotationMirror> findScreenAnnotations() {
        List<AnnotationMirror> annotationMirrors = new ArrayList<>();
        List<AnnotationValue> screenAnnotations = ExtractorUtils.getValueFromAnnotation(element, AutoScreen.class, SCREEN_ANNOTATIONS);
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

    public TypeMirror getComponentDependency() {
        return componentDependency;
    }

    public AnnotationMirror getScopeAnnotationTypeMirror() {
        return scopeAnnotationTypeMirror;
    }

    public AnnotationMirror getComponentAnnotationTypeMirror() {
        return componentAnnotationTypeMirror;
    }

    public List<VariableElement> getConstructorsParamtersElements() {
        return constructorsParamtersElements;
    }

    public List<AnnotationMirror> getScreenAnnotationsMirrors() {
        return screenAnnotationsMirrors;
    }
}
