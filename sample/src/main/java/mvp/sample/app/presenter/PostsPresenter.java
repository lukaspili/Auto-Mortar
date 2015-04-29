package mvp.sample.app.presenter;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import flow.Flow;
import flownavigation.view.Layout;
import mortar.ViewPresenter;
import mvp.navigation.MVP;
import mvp.sample.R;
import mvp.sample.app.adapter.PostAdapter;
import mvp.sample.model.Post;
import mvp.sample.rest.RestClient;
import mvp.sample.ui.activity.RootActivity;
import mvp.sample.ui.view.PostsView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * @author Lukasz Piliszczuk <lukasz.pili@gmail.com>
 */
@MVP(
        parentComponent = RootActivity.Component.class,
        baseViewLayout = FrameLayout.class,
        screenAnnotations = Layout.class
)
@Layout(R.layout.screen_posts)
public class PostsPresenter extends ViewPresenter<PostsView> implements PostAdapter.Listener {

    private final RestClient restClient;

    private PostAdapter adapter;
    private List<Post> posts = new ArrayList<>();

    @Inject
    public PostsPresenter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    protected void onLoad(Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getView().getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getView().recyclerView.setLayoutManager(layoutManager);

        adapter = new PostAdapter(getView().getContext(), posts, this);
        getView().recyclerView.setAdapter(adapter);

        if (posts.isEmpty()) {
            load();
        }
    }

    private void load() {

        restClient.getService().getPosts(new Callback<List<Post>>() {
            @Override
            public void success(List<Post> loadedPosts, Response response) {
                if (!hasView()) return;
                Timber.d("Success loaded %s", loadedPosts.size());

                posts.clear();
                posts.addAll(loadedPosts);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                if (!hasView()) return;
                Timber.d("Failure %s", error.getMessage());
            }
        });
    }

    @Override
    protected void onSave(Bundle outState) {
        super.onSave(outState);


    }

    @Override
    public void onItemClick(int position) {
        if (!hasView()) return;

        Post post = posts.get(position);
        Flow.get(getView()).set(new MVP_ViewPostScreen(post));
    }
}
