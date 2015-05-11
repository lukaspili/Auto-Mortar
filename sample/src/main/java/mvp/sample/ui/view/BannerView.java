package mvp.sample.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import mvp.WithInjector;
import mvp.sample.R;
import mvp.sample.app.presenter.MVP_ViewPostScreen;
import mvp.sample.app.presenter.ViewPostPresenter;
import timber.log.Timber;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@WithInjector(ViewPostPresenter.class)
public class BannerView extends LinearLayout {

    @Inject
    protected ViewPostPresenter presenter;

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        MVP_ViewPostScreen.getComponent(context).inject(this);

        View view = View.inflate(context, R.layout.view_banner, this);
        ButterKnife.inject(view);
    }

    @OnClick(R.id.text)
    void click() {
        Timber.d("CLICK");
        presenter.bannerClick();
    }
}
