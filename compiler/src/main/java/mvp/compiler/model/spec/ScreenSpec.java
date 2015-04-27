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
    private BaseViewSpec baseViewSpec;
    private TypeName viewTypeName;
    private ModuleSpec moduleSpec;
    private ComponentSpec componentSpec;
    private ClassName daggerComponentTypeName;
    private int layout;
    private List<InjectableVariableElement> screenParamMembers;

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

    public BaseViewSpec getBaseViewSpec() {
        return baseViewSpec;
    }

    public void setBaseViewSpec(BaseViewSpec baseViewSpec) {
        this.baseViewSpec = baseViewSpec;
    }

    public TypeName getViewTypeName() {
        return viewTypeName;
    }

    public void setViewTypeName(TypeName viewTypeName) {
        this.viewTypeName = viewTypeName;
    }

    public ModuleSpec getModuleSpec() {
        return moduleSpec;
    }

    public void setModuleSpec(ModuleSpec moduleSpec) {
        this.moduleSpec = moduleSpec;
    }

    public ComponentSpec getComponentSpec() {
        return componentSpec;
    }

    public void setComponentSpec(ComponentSpec componentSpec) {
        this.componentSpec = componentSpec;
    }

    public ClassName getDaggerComponentTypeName() {
        return daggerComponentTypeName;
    }

    public void setDaggerComponentTypeName(ClassName daggerComponentTypeName) {
        this.daggerComponentTypeName = daggerComponentTypeName;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
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
}
