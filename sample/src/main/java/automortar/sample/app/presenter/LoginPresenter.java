package automortar.sample.app.presenter;

import autodagger.AutoComponent;
import automortar.AutoScreen;
import automortar.sample.R;
import automortar.sample.app.AppDependencies;
import automortar.sample.app.DaggerScope;
import automortar.sample.rest.RestClient;
import flownavigation.common.flow.Layout;
import mortar.ViewPresenter;
import timber.log.Timber;

/**
 * @author Lukasz Piliszczuk - lukasz.pili@gmail.com
 */
@AutoScreen(
        component = @AutoComponent(dependencies = PostsPresenter.class, superinterfaces = AppDependencies.class),
        screenAnnotations = Layout.class
)
@DaggerScope(LoginPresenter.class)
@Layout(R.layout.screen_login)
public class LoginPresenter extends ViewPresenter {

    private final RestClient restClient;

    public LoginPresenter(RestClient restClient) {
        this.restClient = restClient;
    }

    public void click() {
        Timber.d("Login screen clicked!");
        // do some stuff with rest client to login
    }
}
