package mvp.compiler.model.spec;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.List;

import javax.lang.model.element.Element;

import mvp.compiler.model.InjectableVariableElement;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ScreenSpec extends AbstractSpec {

    private final Element element;
    private TypeName superclassTypeName;
    private ClassName layoutAnnotationClassName;
    private ModuleSpec moduleSpec;
    private ClassName daggerComponentTypeName;
    private TypeName presenterTypeName;
    private List<InjectableVariableElement> screenParamMembers;
    private List<ScreenAnnotationSpec> annotationSpecs;

    public ScreenSpec(ClassName className, Element element) {
        super(className);
        this.element = element;
    }

    public TypeName getSuperclassTypeName() {
        return superclassTypeName;
    }

    public void setSuperclassTypeName(TypeName superclassTypeName) {
        this.superclassTypeName = superclassTypeName;
    }

    public ClassName getLayoutAnnotationClassName() {
        return layoutAnnotationClassName;
    }

    public void setLayoutAnnotationClassName(ClassName layoutAnnotationClassName) {
        this.layoutAnnotationClassName = layoutAnnotationClassName;
    }

    public ModuleSpec getModuleSpec() {
        return moduleSpec;
    }

    public void setModuleSpec(ModuleSpec moduleSpec) {
        this.moduleSpec = moduleSpec;
    }

    public ClassName getDaggerComponentTypeName() {
        return daggerComponentTypeName;
    }

    public void setDaggerComponentTypeName(ClassName daggerComponentTypeName) {
        this.daggerComponentTypeName = daggerComponentTypeName;
    }

    public TypeName getPresenterTypeName() {
        return presenterTypeName;
    }

    public void setPresenterTypeName(TypeName presenterTypeName) {
        this.presenterTypeName = presenterTypeName;
    }

    public List<InjectableVariableElement> getScreenParamMembers() {
        return screenParamMembers;
    }

    public void setScreenParamMembers(List<InjectableVariableElement> screenParamMembers) {
        this.screenParamMembers = screenParamMembers;
    }

    public Element getElement() {
        return element;
    }

    public List<ScreenAnnotationSpec> getAnnotationSpecs() {
        return annotationSpecs;
    }

    public void setAnnotationSpecs(List<ScreenAnnotationSpec> annotationSpecs) {
        this.annotationSpecs = annotationSpecs;
    }
}
