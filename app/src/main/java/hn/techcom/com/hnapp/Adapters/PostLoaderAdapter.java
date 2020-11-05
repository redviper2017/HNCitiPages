package hn.techcom.com.hnapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.Models.SupporterProfile;
import hn.techcom.com.hnapp.R;

public class PostLoaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static int TYPE_SUPPORTED_PROFILES = 1;
    private static int TYPE_POSTS = 2;

    private ArrayList<Post> postList;
    private ArrayList<SupporterProfile> userSupportedProfiles;
    private Context context;

    private static final String TAG = "PostLoaderAdapter";

    public PostLoaderAdapter(ArrayList<Post> postList, ArrayList<SupporterProfile> userSupportedProfiles, Context context) {
        this.postList = postList;
        this.userSupportedProfiles = userSupportedProfiles;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        //for top item avatar list
        if (viewType == TYPE_SUPPORTED_PROFILES){
            view = LayoutInflater.from(context).inflate(R.layout.row_first_post, parent, false);
            return new SupportedProfilesHolder(view);
        }
        //for all item posts
        else {
            view = LayoutInflater.from(context).inflate(R.layout.row_post,parent,false);
            return new PostsHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_SUPPORTED_PROFILES;
        else
            return TYPE_POSTS;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_SUPPORTED_PROFILES)
            ((SupportedProfilesHolder) holder).setRecyclerView(userSupportedProfiles);
        else
            ((PostsHolder) holder).setPostView(postList.get(position));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class SupportedProfilesHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;

        SupportedProfilesHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerview_supported_avatars_supportsection);
        }

        void setRecyclerView(ArrayList<SupporterProfile> userSupportedProfiles) {
            ArrayList<String> avatarList = new ArrayList<>();
            ArrayList<String> nameList = new ArrayList<>();
            for (SupporterProfile supportingProfile : userSupportedProfiles) {
                avatarList.add(supportingProfile.getProfileImgUrl());
                nameList.add(supportingProfile.getFullName());
            }
            Log.d(TAG, "avatar list size = " + avatarList.size());
            AvatarLoaderAdapter adapter = new AvatarLoaderAdapter(avatarList, nameList);
            LinearLayoutManager horizontalLayout = new LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false
            );
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(horizontalLayout);
            recyclerView.setAdapter(adapter);
        }
    }

    class PostsHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private MaterialTextView userName, userLocation, postTime, supportButton, postBody;
        private ViewPager imageSliderView;

        public PostsHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.circleimageview_postedBy_image);
            userName = itemView.findViewById(R.id.textview_postedby_name);
            userLocation = itemView.findViewById(R.id.textview_postedfrom_location);
            postTime = itemView.findViewById(R.id.textview_postedat_time);
            supportButton = itemView.findViewById(R.id.text_support_post);
            postBody = itemView.findViewById(R.id.textview_post_body);
            imageSliderView = itemView.findViewById(R.id.image_slider_post);
        }

        void setPostView(Post post) {
            String fullname = post.getUser().getFirstName() + " " + post.getUser().getLastName();
            String location = post.getUser().getCity() + ", " + post.getUser().getCountry();

            Picasso.get()
                    .load("http://hn.techcomengine.com" + post.getUser().getProfileImgUrl())
                    .fit()
                    .centerInside()
                    .into(userImage);
            userName.setText(fullname);
            userLocation.setText(location);
            postTime.setText(post.getCreatedOn());
            postBody.setText(post.getText());

            if (post.getType().equals("I")) {
                imageSliderView.setVisibility(View.VISIBLE);

                ArrayList<String> imageList = new ArrayList<>();

                imageList.add(post.getImageUrl());
                ImageLoaderAdapter adapter = new ImageLoaderAdapter(context, imageList);
                imageSliderView.setAdapter(adapter);
            }
            else
                imageSliderView.setVisibility(View.GONE);
        }
    }
}
