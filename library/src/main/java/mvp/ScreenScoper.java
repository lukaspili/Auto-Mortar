package mvp;

import android.content.Context;

import mortar.MortarScope;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public interface ScreenScoper {

    MortarScope getScreenScope(Context context, String name, ComponentFactory componentFactory);
}
