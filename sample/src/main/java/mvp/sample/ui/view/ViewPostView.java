package mvp.sample.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import mvp.sample.R;
import mvp.sample.app.presenter.MVP_ViewPostScreen;

import butterknife.InjectView;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class ViewPostView extends MVP_ViewPostScreen.View {

    @InjectView(R.id.title)
    public TextView titleTextView;

    @InjectView(R.id.content)
    public TextView contentTextView;

    public ViewPostView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
