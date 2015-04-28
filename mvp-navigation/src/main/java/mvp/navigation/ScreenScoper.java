package mvp.navigation;

import android.content.Context;
import android.util.Log;

import flownavigation.path.Path;
import mortar.MortarScope;
import mvp.ComponentFactory;
import mvp.DaggerService;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class ScreenScoper {

    public MortarScope getScreenScope(Context context, String name, Path path) {
        MortarScope parentScope = MortarScope.getScope(context);
        Log.d(getClass().getCanonicalName(), "MVP navigation - Screen scoper with parent " + parentScope.getName());

        MortarScope childScope = parentScope.findChild(name);
        if (childScope != null) {
            Log.d(getClass().getCanonicalName(), "MVP navigation - Screen scoper returns existing scope " + name);
            return childScope;
        }

        if (!(path instanceof ComponentFactory)) {
            throw new IllegalStateException("Path must imlement ComponentFactory");
        }
        ComponentFactory componentFactory = (ComponentFactory) path;

        MortarScope.Builder builder = parentScope.buildChild();
        builder.withService(DaggerService.SERVICE_NAME, componentFactory.createComponent(parentScope.getService(DaggerService.SERVICE_NAME)));

        Log.d(getClass().getCanonicalName(), "MVP navigation - Screen scoper builds and returns new scope " + name);
        return builder.build(name);

    }
}