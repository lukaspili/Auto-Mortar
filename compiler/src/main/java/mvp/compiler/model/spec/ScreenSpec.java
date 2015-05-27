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
    private TypeName presenterTypeName;
    private TypeName superclassTypeName;
    private ClassName componentClassName;
    private ModuleSpec moduleSpec;
    private AnnotationMirror scopeAnnotationMirror; // javapoet handles now AnnotationMirror types
    private List<InjectableVariableElement> constructorParameters;
    private List<ScreenAnnotationSpec> annotationSpecs;
    private List<AutoComponentMemberSpec> componentDependenciesSpecs;
    private List<AutoComponentMemberSpec> componentModulesSpecs;

    public ScreenSpec(ClassName className, Element element) {
        super(className);
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    public TypeName getPresenterTypeName() {
        return presenterTypeName;
    }

    public void setPresenterTypeName(TypeName presenterTypeName) {
        this.presenterTypeName = presenterTypeName;
    }

    public TypeName getSuperclassTypeName() {
        return superclassTypeName;
    }

    public void setSuperclassTypeName(TypeName superclassTypeName) {
        this.superclassTypeName = superclassTypeName;
    }

    public ClassName getComponentClassName() {
        return componentClassName;
    }

    public void setComponentClassName(ClassName componentClassName) {
        this.componentClassName = componentClassName;
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

    public List<InjectableVariableElement> getConstructorParameters() {
        return constructorParameters;
    }

    public void setConstructorParameters(List<InjectableVariableElement> constructorParameters) {
        this.constructorParameters = constructorParameters;
    }

    public List<ScreenAnnotationSpec> getAnnotationSpecs() {
        return annotationSpecs;
    }

    public void setAnnotationSpecs(List<ScreenAnnotationSpec> annotationSpecs) {
        this.annotationSpecs = annotationSpecs;
    }

    public List<AutoComponentMemberSpec> getComponentDependenciesSpecs() {
        return componentDependenciesSpecs;
    }

    public void setComponentDependenciesSpecs(List<AutoComponentMemberSpec> componentDependenciesSpecs) {
        this.componentDependenciesSpecs = componentDependenciesSpecs;
    }

    public List<AutoComponentMemberSpec> getComponentModulesSpecs() {
        return componentModulesSpecs;
    }

    public void setComponentModulesSpecs(List<AutoComponentMemberSpec> componentModulesSpecs) {
        this.componentModulesSpecs = componentModulesSpecs;
    }
}
