package automortar.compiler.model;

import javax.lang.model.element.VariableElement;

/**
 * Holds variable element and indicates if it must be injected through screen navigation
 *
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class InjectableVariableElement {

    private VariableElement element;
    private boolean screenParam;

    public InjectableVariableElement(VariableElement element, boolean screenParam) {
        this.element = element;
        this.screenParam = screenParam;
    }

    public VariableElement getElement() {
        return element;
    }

    public boolean isScreenParam() {
        return screenParam;
    }
}
