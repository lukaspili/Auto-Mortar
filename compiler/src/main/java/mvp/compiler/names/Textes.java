package mvp.compiler.names;

import javax.lang.model.element.Element;

import mortar.ViewPresenter;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public final class Textes {

    public static String getPresenterMustExtendsSuperclassText(Element element) {
        return String.format("%s must now extend from %s<? extends android.view.View>", element.getSimpleName(), ViewPresenter.class.getCanonicalName());
    }
}
