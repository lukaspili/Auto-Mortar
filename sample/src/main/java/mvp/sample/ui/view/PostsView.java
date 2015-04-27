package mvp.sample.ui.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import mvp.sample.R;
import mvp.sample.app.presenter.MVP_PostsScreen;

import butterknife.InjectView;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
public class PostsView extends MVP_PostsScreen.View {

    @InjectView(R.id.recycler_view)
    public RecyclerView recyclerView;

    public PostsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
