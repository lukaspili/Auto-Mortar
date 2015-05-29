package automortar.sample.mortar;

import android.content.Context;

import flownavigation.common.mortar.BasicScreenScoper;
import flownavigation.path.Path;
import mortar.MortarScope;
import automortar.ScreenComponentFactory;
import automortar.sample.app.DaggerService;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ScreenScoper extends BasicScreenScoper {

    @Override
    protected void configureMortarScope(Context context, String name, Path path, MortarScope parentScope, MortarScope.Builder mortarScopeBuilder) {
        if (!(path instanceof ScreenComponentFactory)) {
            throw new IllegalStateException("Path must imlement ComponentFactory");
        }

        ScreenComponentFactory screenComponentFactory = (ScreenComponentFactory) path;
        Object component = screenComponentFactory.createComponent(parentScope.getService(DaggerService.SERVICE_NAME));
        mortarScopeBuilder.withService(DaggerService.SERVICE_NAME, component);
    }
}