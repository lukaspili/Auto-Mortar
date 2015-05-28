package automortar.sample.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

import autodagger.AutoInjector;
import butterknife.ButterKnife;
import butterknife.InjectView;
import mvp.sample.R;
import automortar.sample.app.DaggerService;
import automortar.sample.app.presenter.ViewPostPresenter;
import automortar.sample.app.presenter.ViewPostScreenComponent;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoInjector(ViewPostPresenter.class)
public class ViewPostView extends LinearLayout {

    @Inject
    protected ViewPostPresenter presenter;

    @InjectView(R.id.title)
    public TextView titleTextView;

    @InjectView(R.id.content)
    public TextView contentTextView;

    public ViewPostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerService.<ViewPostScreenComponent>getDaggerComponent(context).inject(this);
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
