package automortar.sample.app.presenter;

import android.os.Bundle;

import autodagger.AutoComponent;
import automortar.sample.R;
import flownavigation.common.flow.Layout;
import mortar.ViewPresenter;
import automortar.AutoScreen;
import automortar.ScreenParam;
import automortar.sample.R;
import automortar.sample.app.DaggerScope;
import automortar.sample.model.Post;
import automortar.sample.ui.activity.RootActivity;
import automortar.sample.ui.view.ViewPostView;
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

    private Post post;

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