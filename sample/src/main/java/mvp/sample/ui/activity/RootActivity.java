package mvp.sample.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.gson.Gson;

import butterknife.ButterKnife;
import butterknife.InjectView;
import flow.Flow;
import flow.FlowDelegate;
import flow.History;
import flownavigation.common.flow.GsonParceler;
import flownavigation.common.flow.HandlesBack;
import flownavigation.path.Path;
import flownavigation.path.PathContainerView;
import mortar.MortarScope;
import mortar.bundler.BundleServiceRunner;
import mvp.sample.R;
import mvp.sample.app.ActivityScope;
import mvp.sample.app.App;
import mvp.sample.app.DaggerService;
import mvp.sample.app.presenter.MVP_PostsScreen;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class RootActivity extends Activity implements Flow.Dispatcher {

    MortarScope mortarScope;

    FlowDelegate flowDelegate;

    @InjectView(R.id.container)
    PathContainerView pathContainerView;

    @Override
    public Object getSystemService(String name) {
        Object service = null;
        if (flowDelegate != null) {
            service = flowDelegate.getSystemService(name);
        }

        if (service == null && mortarScope != null && mortarScope.hasService(name)) {
            service = mortarScope.getService(name);
        }

        return service != null ? service : super.getSystemService(name);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mortarScope = MortarScope.findChild(getApplicationContext(), getClass().getName());

        if (mortarScope == null) {
            Component component = DaggerRootActivity_Component.builder()
                    .component(DaggerService.<App.Component>getDaggerComponent(getApplicationContext()))
                    .build();

            mortarScope = MortarScope.buildChild(getApplicationContext())
                    .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
                    .withService(DaggerService.SERVICE_NAME, component)
                    .build(getClass().getName());
        }

        DaggerService.<Component>getDaggerComponent(this).inject(this);

        BundleServiceRunner.getBundleServiceRunner(this).onCreate(savedInstanceState);

        setContentView(R.layout.activity_root);
        ButterKnife.inject(this);

        GsonParceler parceler = new GsonParceler(new Gson());
        @SuppressWarnings("deprecation") FlowDelegate.NonConfigurationInstance nonConfig =
                (FlowDelegate.NonConfigurationInstance) getLastNonConfigurationInstance();
        flowDelegate = FlowDelegate.onCreate(nonConfig, getIntent(), savedInstanceState, parceler, History.single(new MVP_PostsScreen()), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flowDelegate.onResume();
    }

    @Override
    protected void onPause() {
        flowDelegate.onPause();
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BundleServiceRunner.getBundleServiceRunner(this).onSaveInstanceState(outState);
        flowDelegate.onSaveInstanceState(outState);
    }

    @SuppressWarnings("deprecation") // https://code.google.com/p/android/issues/detail?id=151346
    @Override
    public Object onRetainNonConfigurationInstance() {
        return flowDelegate.onRetainNonConfigurationInstance();
    }

    @Override
    protected void onDestroy() {
        if (isFinishing()) {
            MortarScope activityScope = MortarScope.findChild(getApplicationContext(), getClass().getName());
            if (activityScope != null) {
                activityScope.destroy();
            }
        }

        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (((HandlesBack) pathContainerView).onBackPressed()) return;
        if (flowDelegate.onBackPressed()) return;

        super.onBackPressed();
    }

    // Flow.Dispatcher

    @Override
    public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {
        Path path = traversal.destination.top();
        setTitle(path.getClass().getSimpleName());
        ActionBar actionBar = getActionBar();
        boolean canGoBack = traversal.destination.size() > 1;
        actionBar.setDisplayHomeAsUpEnabled(canGoBack);
        actionBar.setHomeButtonEnabled(canGoBack);

        pathContainerView.dispatch(traversal, new Flow.TraversalCallback() {
            @Override
            public void onTraversalCompleted() {
                invalidateOptionsMenu();
                callback.onTraversalCompleted();
            }
        });
    }

    @dagger.Component(dependencies = App.Component.class)
    @ActivityScope
    public interface Component extends App.Component {

        void inject(RootActivity activity);
    }
}
