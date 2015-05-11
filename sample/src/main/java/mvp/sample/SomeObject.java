package mvp.sample;

import javax.inject.Inject;

import mvp.ScreenScope;
import mvp.WithComponent;
import mvp.sample.app.presenter.PostsPresenter;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@WithComponent(PostsPresenter.class)
@ScreenScope(PostsPresenter.class)
public class SomeObject {

    @Inject
    public SomeObject() {
    }
}
