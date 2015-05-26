package mvp.sample.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import javax.inject.Inject;

import autodagger.autodagger.AutoInjector;
import butterknife.ButterKnife;
import butterknife.InjectView;
import mvp.sample.R;
import mvp.sample.app.presenter.PostsPresenter;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoInjector(PostsPresenter.class)
public class PostsView extends FrameLayout {

    @Inject
    protected PostsPresenter presenter;

    @InjectView(R.id.recycler_view)
    public RecyclerView recyclerView;

    public PostsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.takeView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.dropView(this);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onFinishInflate() {
        ButterKnife.inject(this);
    }
}
