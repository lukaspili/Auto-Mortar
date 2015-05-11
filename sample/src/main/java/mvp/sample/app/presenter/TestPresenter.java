package mvp.sample.app.presenter;

import android.widget.LinearLayout;

import javax.inject.Inject;

import mortar.ViewPresenter;
import mvp.MVP;
import mvp.sample.ui.view.TestView;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@MVP(
        parent = PostsPresenter.class,
        baseViewLayout = LinearLayout.class
)
public class TestPresenter extends ViewPresenter<TestView> {

    @Inject
    public TestPresenter() {
    }
}
