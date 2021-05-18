package hn.techcom.com.hnapp.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.santalu.aspectratioimageview.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hnapp.Models.Result;
import hn.techcom.com.hnapp.R;

public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //Constants
    private static final String TAG = "PostListAdapter";
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_STORY = 1;
    private static final int VIEW_TYPE_IMAGE = 2;
    private static final int VIEW_TYPE_VIDEO = 3;
    private static final int VIEW_TYPE_AUDIO = 4;

    private OnLoadMoreListener onLoadMoreListener;
    private  ArrayList<Result> allPosts = new ArrayList<>();

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public PostListAdapter(RecyclerView recyclerView, ArrayList<Result> allPosts){
        this.allPosts = allPosts;

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
                    }
                    isLoading = true;
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_STORY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_story_post, parent, false);
                return new StoryViewHolder(view);

            case VIEW_TYPE_IMAGE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_image_post, parent, false);
                return new ImageViewHolder(view);
            case VIEW_TYPE_VIDEO:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_video_post, parent, false);
                return new VideoViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_loading_post, parent, false);
                return new LoadingViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof StoryViewHolder){
            Result post = allPosts.get(position);

            StoryViewHolder storyViewHolder = (StoryViewHolder) holder;

            String location = post.getUser().getCity() + ", " + post.getUser().getCountry();

            storyViewHolder.name.setText(post.getUser().getFullName());
            storyViewHolder.location.setText(location);
            storyViewHolder.text.setText(post.getText());

            //see more button toggle for large texts
//            if( storyViewHolder.text.getLayout().getLineCount() > 10)
//                storyViewHolder.seeMoreButton.setVisibility(View.VISIBLE);
//            else
//                storyViewHolder.seeMoreButton.setVisibility(View.GONE);

            String profilePhotoUrl = "http://167.99.13.238:8000" + post.getUser().getProfileImg();
            Picasso
                    .get()
                    .load(profilePhotoUrl)
                    .into(storyViewHolder.avatar);
        }
        if(holder instanceof ImageViewHolder){
            Result post = allPosts.get(position);

            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;

            String location = post.getUser().getCity() + ", " + post.getUser().getCountry();

            imageViewHolder.name.setText(post.getUser().getFullName());
            imageViewHolder.location.setText(location);
            imageViewHolder.text.setText(post.getText());

            //see more button toggle for large texts
//            if( imageViewHolder.text.getLayout().getLineCount() > 10)
//                imageViewHolder.seeMoreButton.setVisibility(View.VISIBLE);
//            else
//                imageViewHolder.seeMoreButton.setVisibility(View.GONE);

            String profilePhotoUrl = "http://167.99.13.238:8000" + post.getUser().getProfileImg();
            Picasso
                    .get()
                    .load(profilePhotoUrl)
                    .into(imageViewHolder.avatar);

            String imageUrl = "http://167.99.13.238:8000" + post.getFiles().get(0).getMedia();

            //TODO: later toggle the imageview based on image aspect ratio
            imageViewHolder.landscapeImageView.setVisibility(View.GONE);
            imageViewHolder.portraitImageView.setVisibility(View.VISIBLE);
            Picasso
                    .get()
                    .load(imageUrl)
                    .into(imageViewHolder.portraitImageView);

            //Placing image into respective imageview based on aspect ratio
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (allPosts.get(position) == null)
            return VIEW_TYPE_LOADING;
        else {
            switch (allPosts.get(position).getPosttype()){
                case "S":
                    return VIEW_TYPE_STORY;
                case "I":
                    return VIEW_TYPE_IMAGE;
                case "V":
                    return VIEW_TYPE_VIDEO;
                case "A":
                    return VIEW_TYPE_AUDIO;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return allPosts == null ? 0 : allPosts.size();
    }

    //Custom methods
    public void setLoaded() {
        isLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.onLoadMoreListener = mOnLoadMoreListener;
    }

    //View holder classes

    //Story view holder class
    private class StoryViewHolder extends RecyclerView.ViewHolder{

        public MaterialTextView name, location, text, likes, comments, seeMoreButton;
        public CircleImageView avatar;

        public StoryViewHolder(@NonNull View view) {
            super(view);

            name     = view.findViewById(R.id.name_post);
            location = view.findViewById(R.id.location_post);
            text     = view.findViewById(R.id.text_post);
            likes    = view.findViewById(R.id.text_like_count_post);
            comments = view.findViewById(R.id.text_comment_count_post);
            avatar   = view.findViewById(R.id.avatar_post);
        }
    }

    //Image view holder class
    private class ImageViewHolder extends RecyclerView.ViewHolder{

        public MaterialTextView name, location, text, likes, comments, seeMoreButton;
        public CircleImageView avatar;
        public AspectRatioImageView landscapeImageView, portraitImageView;

        public ImageViewHolder(@NonNull View view) {
            super(view);

            name               = view.findViewById(R.id.name_post);
            location           = view.findViewById(R.id.location_post);
            text               = view.findViewById(R.id.text_post);
            likes              = view.findViewById(R.id.text_like_count_post);
            comments           = view.findViewById(R.id.text_comment_count_post);
            avatar             = view.findViewById(R.id.avatar_post);
            landscapeImageView = view.findViewById(R.id.imageview_landscape_post);
            portraitImageView  = view.findViewById(R.id.imageview_portrait_post);
        }
    }

    //Video view holder class
    private class VideoViewHolder extends RecyclerView.ViewHolder{

        public MaterialTextView name, location, text, likes, comments, seeMoreButton;
        public CircleImageView avatar;
        public VideoView landscapeVideoView, portraitVideoView;

        public VideoViewHolder(@NonNull View view) {
            super(view);

            name               = view.findViewById(R.id.name_post);
            location           = view.findViewById(R.id.location_post);
            text               = view.findViewById(R.id.text_post);
            likes              = view.findViewById(R.id.text_like_count_post);
            comments           = view.findViewById(R.id.text_comment_count_post);
            avatar             = view.findViewById(R.id.avatar_post);
            landscapeVideoView = view.findViewById(R.id.videoview_landscape_post);
            portraitVideoView  = view.findViewById(R.id.videoview_portrait_post);
        }
    }

    //Loading view holder class
    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);

            progressBar = view.findViewById(R.id.progressbar);
        }
    }
}