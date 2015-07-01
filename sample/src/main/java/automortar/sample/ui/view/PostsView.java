package automortar.sample.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import javax.inject.Inject;

import autodagger.AutoInjector;
import automortar.sample.R;
import automortar.sample.app.DaggerService;
import automortar.sample.app.presenter.PostsPresenter;
import automortar.sample.app.presenter.screen.PostsScreenComponent;
import butterknife.ButterKnife;
import butterknife.InjectView;

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
        DaggerService.<PostsScreenComponent>getDaggerComponent(context).inject(this);
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
