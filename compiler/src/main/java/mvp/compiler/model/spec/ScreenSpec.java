package mvp.compiler.model.spec;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import mvp.compiler.model.InjectableVariableElement;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ScreenSpec extends AbstractSpec {

    private final Element element;
    private TypeName superclassTypeName;
    private ModuleSpec moduleSpec;
    private AnnotationMirror scopeAnnotationMirror; // javapoet handles now AnnotationMirror types
    private List<InjectableVariableElement> screenParamMembers;
    private List<ScreenAnnotationSpec> annotationSpecs;
    private List<TypeName> componentDependenciesTypeNames;
    private List<TypeName> componentModulesTypeNames;

    public ScreenSpec(ClassName className, Element element) {
        super(className);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public TypeName getSuperclassTypeName() {
        return superclassTypeName;
    }

    public void setSuperclassTypeName(TypeName superclassTypeName) {
        this.superclassTypeName = superclassTypeName;
    }

    public ModuleSpec getModuleSpec() {
        return moduleSpec;
    }

    public void setModuleSpec(ModuleSpec moduleSpec) {
        this.moduleSpec = moduleSpec;
    }

    public AnnotationMirror getScopeAnnotationMirror() {
        return scopeAnnotationMirror;
    }

    public void setScopeAnnotationMirror(AnnotationMirror scopeAnnotationMirror) {
        this.scopeAnnotationMirror = scopeAnnotationMirror;
    }

    public List<InjectableVariableElement> getScreenParamMembers() {
        return screenParamMembers;
    }

    public void setScreenParamMembers(List<InjectableVariableElement> screenParamMembers) {
        this.screenParamMembers = screenParamMembers;
    }

    public List<ScreenAnnotationSpec> getAnnotationSpecs() {
        return annotationSpecs;
    }

    public void setAnnotationSpecs(List<ScreenAnnotationSpec> annotationSpecs) {
        this.annotationSpecs = annotationSpecs;
    }

    public List<TypeName> getComponentDependenciesTypeNames() {
        return componentDependenciesTypeNames;
    }

    public void setComponentDependenciesTypeNames(List<TypeName> componentDependenciesTypeNames) {
        this.componentDependenciesTypeNames = componentDependenciesTypeNames;
    }

    public List<TypeName> getComponentModulesTypeNames() {
        return componentModulesTypeNames;
    }

    public void setComponentModulesTypeNames(List<TypeName> componentModulesTypeNames) {
        this.componentModulesTypeNames = componentModulesTypeNames;
    }
}
