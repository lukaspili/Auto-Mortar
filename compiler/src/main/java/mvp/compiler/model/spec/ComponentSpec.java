package mvp.compiler.model.spec;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ComponentSpec extends AbstractSpec {

    private TypeName parentTypeName;
    private TypeName moduleTypeName;
    private TypeName viewTypeName;

    public ComponentSpec(ClassName className) {
        super(className);
    }

    public TypeName getParentTypeName() {
        return parentTypeName;
    }

    public void setParentTypeName(TypeName parentTypeName) {
        this.parentTypeName = parentTypeName;
    }

    public TypeName getModuleTypeName() {
        return moduleTypeName;
    }

    public void setModuleTypeName(TypeName moduleTypeName) {
        this.moduleTypeName = moduleTypeName;
    }

    public TypeName getViewTypeName() {
        return viewTypeName;
    }

    public void setViewTypeName(TypeName viewTypeName) {
        this.viewTypeName = viewTypeName;
    }
}
