package hn.techcom.com.hnapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Activities.ViewLikesActivity;
import hn.techcom.com.hnapp.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hnapp.Models.Result;
import hn.techcom.com.hnapp.Models.Result1;
import hn.techcom.com.hnapp.R;

public class LikeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //Constants
    private static final String TAG = "LikeListAdapter";
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<Result1> allLikes = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;

    public LikeListAdapter(RecyclerView recyclerView, ArrayList<Result1> allLikes,  Context context) {
        this.recyclerView = recyclerView;
        this.allLikes = allLikes;
        this.context = context;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_likes, parent, false);
        return new LikeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Result1 like = allLikes.get(position);
        ((LikeViewHolder) holder).bind(like);
    }

    @Override
    public int getItemCount() {
        return allLikes == null ? 0 : allLikes.size();
    }

    private class LikeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location;
        public CircleImageView avatar;

        public LikeViewHolder(@NonNull View view) {
            super(view);

            name           = view.findViewById(R.id.name_post);
            location       = view.findViewById(R.id.location_post);
            avatar         = view.findViewById(R.id.avatar_post);
        }

        void bind(Result1 like) {
            String address = like.getUser().getCity() + ", " + like.getUser().getCountry();

            //setting up user name and location
            name.setText(like.getUser().getFullName());
            location.setText(address);

            //setting up user avatar
            String profilePhotoUrl = "http://167.99.13.238:8000" + like.getUser().getProfileImg();
            Picasso
                    .get()
                    .load(profilePhotoUrl)
                    .into(avatar);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
