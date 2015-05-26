package mvp.sample.app.presenter;

import android.os.Bundle;

import javax.inject.Inject;

import autodagger.autodagger.AutoComponent;
import flownavigation.common.flow.Layout;
import mortar.ViewPresenter;
import mvp.AutoScreen;
import mvp.ScreenParam;
import mvp.sample.R;
import mvp.sample.app.DaggerScope;
import mvp.sample.model.Post;
import mvp.sample.ui.activity.RootActivity;
import mvp.sample.ui.view.ViewPostView;
import timber.log.Timber;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoScreen(
        component = @AutoComponent(dependencies = RootActivity.class),
        screenAnnotations = Layout.class
)
@DaggerScope(ViewPostPresenter.class)
@Layout(R.layout.screen_view_post)
public class ViewPostPresenter extends ViewPresenter<ViewPostView> {

    private final Post post;

    @Inject
    public ViewPostPresenter(@ScreenParam Post post) {
        this.post = post;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        getView().titleTextView.setText(post.getTitle());
        getView().contentTextView.setText(post.getBody());
    }

    public void bannerClick() {
        Timber.d("Banner click !");
    }
}