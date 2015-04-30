package mvp.sample.mortar;

import android.content.Context;

import flownavigation.common.mortar.BasicScreenScoper;
import flownavigation.path.Path;
import mortar.MortarScope;
import mvp.ComponentFactory;
import mvp.DaggerService;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ScreenScoper extends BasicScreenScoper {

    @Override
    protected void configureMortarScope(Context context, String name, Path path, MortarScope parentScope, MortarScope.Builder mortarScopeBuilder) {
        if (!(path instanceof ComponentFactory)) {
            throw new IllegalStateException("Path must imlement ComponentFactory");
        }

        ComponentFactory componentFactory = (ComponentFactory) path;
        Object component = componentFactory.createComponent(parentScope.getService(DaggerService.SERVICE_NAME));
        mortarScopeBuilder.withService(DaggerService.SERVICE_NAME, component);
    }
}