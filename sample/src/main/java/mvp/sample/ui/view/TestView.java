package mvp.sample.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import mvp.sample.app.presenter.MVP_TestScreen;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class TestView extends MVP_TestScreen.View {

    public TestView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
