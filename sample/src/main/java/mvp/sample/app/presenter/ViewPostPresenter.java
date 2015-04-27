package mvp.sample.app.presenter;

import android.os.Bundle;
import android.widget.LinearLayout;

import javax.inject.Inject;

import mortar.ViewPresenter;
import mvp.ScreenParam;
import mvp.navigation.MVP;
import mvp.sample.R;
import mvp.sample.model.Post;
import mvp.sample.ui.activity.RootActivity;
import mvp.sample.ui.view.ViewPostView;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@MVP(
        parentComponent = RootActivity.Component.class,
        baseViewLayout = LinearLayout.class,
        layout = R.layout.screen_view_post
)
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
}
