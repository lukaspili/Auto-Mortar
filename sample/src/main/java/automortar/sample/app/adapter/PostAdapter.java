package automortar.sample.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import automortar.sample.R;
import automortar.sample.model.Post;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private final Context context;
    private final List<Post> posts;
    private final Listener listener;

    private String addPlaceWithQuery;

    public PostAdapter(Context context, List<Post> posts, Listener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_post, viewGroup, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Post post = posts.get(i);
        viewHolder.textView.setText(post.getTitle());
    }

    @Override
    public int getItemCount() {
        return addPlaceWithQuery != null ? posts.size() + 1 : posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.text)
        public TextView textView;

        private Listener listener;

        public ViewHolder(View view, Listener listener) {
            super(view);
            this.listener = listener;

            ButterKnife.inject(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    public interface Listener {
        void onItemClick(int position);
    }

}