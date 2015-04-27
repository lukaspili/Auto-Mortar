package mvp.navigation;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.LayoutInflater;

import mortar.MortarScope;
import flownavigation.path.Path;
import flownavigation.path.PathContextFactory;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
public class MortarContextFactory implements PathContextFactory {

    private final ScreenScoper screenScoper;

    public MortarContextFactory() {
        screenScoper = new ScreenScoper();
    }

    @Override
    public Context setUpContext(Path path, Context parentContext) {
        MortarScope scope = screenScoper.getScreenScope(parentContext, path.getClass().getName(), path);
        return new TearDownContext(parentContext, scope);
    }

    @Override
    public void tearDownContext(Context context) {
        TearDownContext.destroyScope(context);
    }

    static class TearDownContext extends ContextWrapper {
        private static final String SERVICE = "SNEAKY_MORTAR_PARENT_HOOK";
        private final MortarScope parentScope;
        private LayoutInflater inflater;

        static void destroyScope(Context context) {
            MortarScope scope = MortarScope.getScope(context);
            Log.d(MortarContextFactory.class.getCanonicalName(), "MVP navigation - Destroy scope " + scope.getName());
            scope.destroy();
        }

        public TearDownContext(Context context, MortarScope scope) {
            super(scope.createContext(context));
            this.parentScope = MortarScope.getScope(context);
        }

        @Override
        public Object getSystemService(String name) {
            if (LAYOUT_INFLATER_SERVICE.equals(name)) {
                if (inflater == null) {
                    inflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
                }
                return inflater;
            }

            if (SERVICE.equals(name)) {
                return parentScope;
            }

            return super.getSystemService(name);
        }
    }
}
