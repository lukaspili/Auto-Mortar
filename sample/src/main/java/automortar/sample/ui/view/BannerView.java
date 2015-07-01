package automortar.sample.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import javax.inject.Inject;

import autodagger.AutoInjector;
import automortar.sample.R;
import automortar.sample.app.presenter.ViewPostPresenter;
import automortar.sample.app.presenter.screen.ViewPostScreen;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@AutoInjector(ViewPostPresenter.class)
public class BannerView extends LinearLayout {

    @Inject
    protected ViewPostPresenter presenter;

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        ViewPostScreen.getComponent(context).inject(this);

        View view = View.inflate(context, R.layout.view_banner, this);
        ButterKnife.inject(view);
    }

    @OnClick(R.id.text)
    void click() {
        presenter.bannerClick();
    }
}
