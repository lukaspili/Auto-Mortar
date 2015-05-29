package automortar.sample.mortar;

import android.content.Context;
import android.util.AttributeSet;

import flownavigation.common.flow.FramePathContainerView;
import flownavigation.common.flow.SimplePathContainer;
import flownavigation.common.mortar.BasicMortarContextFactory;
import flownavigation.path.Path;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class MortarPathContainerView extends FramePathContainerView {

    public MortarPathContainerView(Context context, AttributeSet attrs) {
        super(context, attrs, new SimplePathContainer(flownavigation.common.flow.R.id.screen_switcher_tag, Path.contextFactory(new BasicMortarContextFactory(new ScreenScoper()))));
    }
}
