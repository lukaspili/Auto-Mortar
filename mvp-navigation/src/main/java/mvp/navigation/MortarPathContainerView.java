package mvp.navigation;

import android.content.Context;
import android.util.AttributeSet;

import flownavigation.path.Path;
import flownavigation.path.SimplePathContainer;
import flownavigation.view.FramePathContainerView;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class MortarPathContainerView extends FramePathContainerView {

    public MortarPathContainerView(Context context, AttributeSet attrs) {
        super(context, attrs, new SimplePathContainer(flownavigation.R.id.screen_switcher_tag, Path.contextFactory(new MortarContextFactory())));
    }
}
