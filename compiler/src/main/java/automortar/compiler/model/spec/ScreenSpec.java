package automortar.compiler.model.spec;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import automortar.compiler.model.InjectableVariableElement;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ScreenSpec extends AbstractSpec {

    private final Element element;
    private TypeName presenterTypeName;
    private TypeName superclassTypeName;
    private ClassName componentClassName;
    private automortar.compiler.model.spec.ModuleSpec moduleSpec;
    private AnnotationMirror scopeAnnotationMirror; // javapoet handles now AnnotationMirror types
    private List<InjectableVariableElement> constructorParameters;
    private List<ScreenAnnotationSpec> annotationSpecs;
    private List<AutoComponentMemberSpec> componentDependenciesSpecs;
    private List<AutoComponentMemberSpec> componentModulesSpecs;
    private List<AutoComponentMemberSpec> componentSuperinterfacesSpecs;

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

    public automortar.compiler.model.spec.ModuleSpec getModuleSpec() {
        return moduleSpec;
    }

    public void setModuleSpec(automortar.compiler.model.spec.ModuleSpec moduleSpec) {
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

    public List<automortar.compiler.model.spec.ScreenAnnotationSpec> getAnnotationSpecs() {
        return annotationSpecs;
    }

    public void setAnnotationSpecs(List<automortar.compiler.model.spec.ScreenAnnotationSpec> annotationSpecs) {
        this.annotationSpecs = annotationSpecs;
    }

    public List<automortar.compiler.model.spec.AutoComponentMemberSpec> getComponentDependenciesSpecs() {
        return componentDependenciesSpecs;
    }

    public void setComponentDependenciesSpecs(List<automortar.compiler.model.spec.AutoComponentMemberSpec> componentDependenciesSpecs) {
        this.componentDependenciesSpecs = componentDependenciesSpecs;
    }

    public List<automortar.compiler.model.spec.AutoComponentMemberSpec> getComponentModulesSpecs() {
        return componentModulesSpecs;
    }

    public void setComponentModulesSpecs(List<automortar.compiler.model.spec.AutoComponentMemberSpec> componentModulesSpecs) {
        this.componentModulesSpecs = componentModulesSpecs;
    }

    public List<AutoComponentMemberSpec> getComponentSuperinterfacesSpecs() {
        return componentSuperinterfacesSpecs;
    }

    public void setComponentSuperinterfacesSpecs(List<AutoComponentMemberSpec> componentSuperinterfacesSpecs) {
        this.componentSuperinterfacesSpecs = componentSuperinterfacesSpecs;
    }
}
