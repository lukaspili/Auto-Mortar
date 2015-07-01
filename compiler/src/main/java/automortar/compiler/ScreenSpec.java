package automortar.compiler;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.List;

import automortar.compiler.ModuleSpec;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ScreenSpec {

    private final ClassName className;
    private ModuleSpec moduleSpec;
    private ConfigSpec configSpec;
    private String daggerComponentBuilderDependencyMethodName;
    private ClassName componentClassName;
    private TypeName daggerComponentBuilderDependencyTypeName;
    private TypeName daggerComponentTypeName;
    private TypeName parentComponentTypeName;
    private AnnotationSpec scopeAnnotationSpec;
    private AnnotationSpec componentAnnotationSpec;
    private final List<AnnotationSpec> screenAnnotationSpecs = new ArrayList<>();

    public ScreenSpec(ClassName className) {
        this.className = className;
    }

    public ClassName getClassName() {
        return className;
    }

    public ModuleSpec getModuleSpec() {
        return moduleSpec;
    }

    public void setModuleSpec(ModuleSpec moduleSpec) {
        this.moduleSpec = moduleSpec;
    }

    public ConfigSpec getConfigSpec() {
        return configSpec;
    }

    public void setConfigSpec(ConfigSpec configSpec) {
        this.configSpec = configSpec;
    }

    public String getDaggerComponentBuilderDependencyMethodName() {
        return daggerComponentBuilderDependencyMethodName;
    }

    public void setDaggerComponentBuilderDependencyMethodName(String daggerComponentBuilderDependencyMethodName) {
        this.daggerComponentBuilderDependencyMethodName = daggerComponentBuilderDependencyMethodName;
    }

    public ClassName getComponentClassName() {
        return componentClassName;
    }

    public void setComponentClassName(ClassName componentClassName) {
        this.componentClassName = componentClassName;
    }

    public TypeName getDaggerComponentBuilderDependencyTypeName() {
        return daggerComponentBuilderDependencyTypeName;
    }

    public void setDaggerComponentBuilderDependencyTypeName(TypeName daggerComponentBuilderDependencyTypeName) {
        this.daggerComponentBuilderDependencyTypeName = daggerComponentBuilderDependencyTypeName;
    }

    public TypeName getDaggerComponentTypeName() {
        return daggerComponentTypeName;
    }

    public void setDaggerComponentTypeName(TypeName daggerComponentTypeName) {
        this.daggerComponentTypeName = daggerComponentTypeName;
    }

    public TypeName getParentComponentTypeName() {
        return parentComponentTypeName;
    }

    public void setParentComponentTypeName(TypeName parentComponentTypeName) {
        this.parentComponentTypeName = parentComponentTypeName;
    }

    public AnnotationSpec getScopeAnnotationSpec() {
        return scopeAnnotationSpec;
    }

    public void setScopeAnnotationSpec(AnnotationSpec scopeAnnotationSpec) {
        this.scopeAnnotationSpec = scopeAnnotationSpec;
    }

    public AnnotationSpec getComponentAnnotationSpec() {
        return componentAnnotationSpec;
    }

    public void setComponentAnnotationSpec(AnnotationSpec componentAnnotationSpec) {
        this.componentAnnotationSpec = componentAnnotationSpec;
    }

    public List<AnnotationSpec> getScreenAnnotationSpecs() {
        return screenAnnotationSpecs;
    }
}
