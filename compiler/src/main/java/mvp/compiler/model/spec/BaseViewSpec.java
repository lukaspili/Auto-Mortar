package mvp.compiler.model.spec;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class BaseViewSpec extends AbstractSpec {

    private TypeName viewTypeName;
    private TypeName superclassTypeName;
    private ClassName componentClassName;
    private ClassName presenterClassName;
    private String presenterMustExtendsViewPresenterWarning;
    private boolean butterknife;

    public BaseViewSpec(ClassName className) {
        super(className);
    }

    public TypeName getViewTypeName() {
        return viewTypeName;
    }

    public void setViewTypeName(TypeName viewTypeName) {
        this.viewTypeName = viewTypeName;
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

    public ClassName getPresenterClassName() {
        return presenterClassName;
    }

    public void setPresenterClassName(ClassName presenterClassName) {
        this.presenterClassName = presenterClassName;
    }

    public String getPresenterMustExtendsViewPresenterWarning() {
        return presenterMustExtendsViewPresenterWarning;
    }

    public void setPresenterMustExtendsViewPresenterWarning(String presenterMustExtendsViewPresenterWarning) {
        this.presenterMustExtendsViewPresenterWarning = presenterMustExtendsViewPresenterWarning;
    }

    public boolean isButterknife() {
        return butterknife;
    }

    public void setButterknife(boolean butterknife) {
        this.butterknife = butterknife;
    }
}
