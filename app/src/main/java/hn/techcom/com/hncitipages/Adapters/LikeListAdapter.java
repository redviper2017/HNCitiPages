package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hncitipages.Interfaces.ViewProfileListener;
import hn.techcom.com.hncitipages.Models.ResultViewLikes;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class LikeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //Constants
    private static final String TAG = "LikeListAdapter";
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private Utils myUtils;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<ResultViewLikes> allLikes = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;
    private ViewProfileListener viewProfileListener;

    public LikeListAdapter(
            RecyclerView recyclerView,
            ArrayList<ResultViewLikes> allLikes,
            Context context,
            ViewProfileListener viewProfileListener) {
        this.recyclerView = recyclerView;
        this.allLikes = allLikes;
        this.context = context;
        this.viewProfileListener = viewProfileListener;

        myUtils = new Utils();

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
        ResultViewLikes like = allLikes.get(position);
        ((LikeViewHolder) holder).bind(like);
    }

    @Override
    public int getItemCount() {
        return allLikes == null ? 0 : allLikes.size();
    }

    private class LikeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location, title;
        public CircleImageView avatar;

        public LikeViewHolder(@NonNull View view) {
            super(view);

            name           = view.findViewById(R.id.name_post);
            title          = view.findViewById(R.id.title_post);
            location       = view.findViewById(R.id.location_post);
            avatar         = view.findViewById(R.id.avatar_post);

            //Click listeners
            name.setOnClickListener(this);
            avatar.setOnClickListener(this);
        }

        void bind(ResultViewLikes like) {
            String address = like.getUser().getCity() + ", " + like.getUser().getCountry();
            String user_title = like.getUser().getTitle();

            //setting up user name and location
            name.setText(myUtils.capitalizeName(like.getUser().getFullName()));

            if (!user_title.equals("User")){
                title.setVisibility(View.VISIBLE);
                location.setVisibility(View.GONE);

                String user_title_text = user_title + ", HN CitiPages";
                title.setText(user_title_text);
            }else {
                if (like.getUser().getCity().equals("N/A") || like.getUser().getCountry().equals("N/A"))
                    location.setVisibility(View.GONE);
                else {
                    location.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    location.setText(address);
                }
            }

            //setting up user avatar
            String profilePhotoUrl = like.getUser().getProfileImgThumbnail();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(avatar);

            Glide.with(context).load(profilePhotoUrl).centerCrop().into(avatar);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.name_post || view.getId() == R.id.avatar_post) {
                int position = getAbsoluteAdapterPosition();
                User user = allLikes.get(position).getUser();

                String hnid = user.getHnid();
                String name = user.getFullName();
                boolean isSupported = user.getIsSupported();

                viewProfileListener.viewProfile(hnid, name, isSupported);
            }
        }
    }
}
