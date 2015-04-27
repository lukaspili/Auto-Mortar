package mvp.compiler.model.spec;

import com.squareup.javapoet.ClassName;

import java.util.List;

import mvp.compiler.model.InjectableVariableElement;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ModuleSpec extends AbstractSpec {

    private ClassName presenterClassName;
    private List<InjectableVariableElement> providePresenterParams;
    private List<InjectableVariableElement> providePresenterConstructorParams;

    public ModuleSpec(ClassName className) {
        super(className);
    }

    public ClassName getPresenterClassName() {
        return presenterClassName;
    }

    public void setPresenterClassName(ClassName presenterClassName) {
        this.presenterClassName = presenterClassName;
    }

    public List<InjectableVariableElement> getProvidePresenterParams() {
        return providePresenterParams;
    }

    public void setProvidePresenterParams(List<InjectableVariableElement> providePresenterParams) {
        this.providePresenterParams = providePresenterParams;
    }

    public List<InjectableVariableElement> getProvidePresenterConstructorParams() {
        return providePresenterConstructorParams;
    }

    public void setProvidePresenterConstructorParams(List<InjectableVariableElement> providePresenterConstructorParams) {
        this.providePresenterConstructorParams = providePresenterConstructorParams;
    }
}
