package mvp.sample.app;

import android.app.Application;

import autodagger.AutoComponent;
import autodagger.AutoInjector;
import mortar.MortarScope;
import mvp.sample.BuildConfig;
import timber.log.Timber;

@AutoComponent
@AutoInjector
@DaggerScope(App.class)
public class App extends Application {

    private MortarScope mortarScope;

    @Override
    public Object getSystemService(String name) {
        return mortarScope.hasService(name) ? mortarScope.getService(name) : super.getSystemService(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        AppComponent component = DaggerAppComponent.builder()
                .build();
        component.inject(this);

        mortarScope = MortarScope.buildRootScope()
                .withService(DaggerService.SERVICE_NAME, component)
                .build("Root");
    }
}