package hn.techcom.com.hnapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;
import com.santalu.aspectratioimageview.AspectRatioImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Interfaces.OnLikeButtonClickListener;
import hn.techcom.com.hnapp.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hnapp.Interfaces.OnOptionsButtonClickListener;
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
    private Context context;

    //instance of interface
    private final OnOptionsButtonClickListener onOptionsButtonClickListener;
    private final OnLikeButtonClickListener onLikeButtonClickListener;

    public PostListAdapter(RecyclerView recyclerView, ArrayList<Result> allPosts, Context context, OnOptionsButtonClickListener onOptionsButtonClickListener, OnLikeButtonClickListener onLikeButtonClickListener){
        this.allPosts = allPosts;
        this.context = context;
        this.onOptionsButtonClickListener = onOptionsButtonClickListener;
        this.onLikeButtonClickListener = onLikeButtonClickListener;

        Log.d(TAG,"post list size in adapter = "+allPosts.size());

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
        Result post = allPosts.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_STORY:
                ((StoryViewHolder) holder).bind(post);
                break;
            case VIEW_TYPE_IMAGE:
                ((ImageViewHolder) holder).bind(post);
                break;
            case VIEW_TYPE_VIDEO:
                ((VideoViewHolder) holder).bind(post);
                break;
            case VIEW_TYPE_LOADING:
                ((LoadingViewHolder) holder).bind();
                break;
        }
//        if(holder instanceof StoryViewHolder){
//            Result post = allPosts.get(position);
//
//            StoryViewHolder storyViewHolder = (StoryViewHolder) holder;
//
//            String location = post.getUser().getCity() + ", " + post.getUser().getCountry();
//
//            storyViewHolder.name.setText(post.getUser().getFullName());
//            storyViewHolder.location.setText(location);
//            storyViewHolder.text.setText(post.getText());
//
//            //see more button toggle for large texts
////            if( storyViewHolder.text.getLayout().getLineCount() > 10)
////                storyViewHolder.seeMoreButton.setVisibility(View.VISIBLE);
////            else
////                storyViewHolder.seeMoreButton.setVisibility(View.GONE);
//
//            String profilePhotoUrl = "http://167.99.13.238:8000" + post.getUser().getProfileImg();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(storyViewHolder.avatar);
//        }
//        if(holder instanceof ImageViewHolder){
//            Result post = allPosts.get(position);
//
//            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
//
//            String location = post.getUser().getCity() + ", " + post.getUser().getCountry();
//
//            imageViewHolder.name.setText(post.getUser().getFullName());
//            imageViewHolder.location.setText(location);
//            imageViewHolder.text.setText(post.getText());
//
//            //see more button toggle for large texts
////            if( imageViewHolder.text.getLayout().getLineCount() > 10)
////                imageViewHolder.seeMoreButton.setVisibility(View.VISIBLE);
////            else
////                imageViewHolder.seeMoreButton.setVisibility(View.GONE);
//
//            String profilePhotoUrl = "http://167.99.13.238:8000" + post.getUser().getProfileImg();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(imageViewHolder.avatar);
//
//            String imageUrl = "http://167.99.13.238:8000" + post.getFiles().get(0).getMedia();
//
//            //TODO: later toggle the imageview based on image aspect ratio
//            imageViewHolder.landscapeImageView.setVisibility(View.GONE);
//            imageViewHolder.portraitImageView.setVisibility(View.VISIBLE);
//            Picasso
//                    .get()
//                    .load(imageUrl)
//                    .into(imageViewHolder.portraitImageView);
//
//            //Placing image into respective imageview based on aspect ratio
//        }
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

    public void setLoadMore(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    //View holder classes

    //Story view holder class
    private class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location, text, likes, comments, seeMoreButton;
        public CircleImageView avatar;
        private ImageButton optionsButton, likeButton;

        public StoryViewHolder(@NonNull View view) {
            super(view);

            name          = view.findViewById(R.id.name_post);
            location      = view.findViewById(R.id.location_post);
            text          = view.findViewById(R.id.text_post);
            likes         = view.findViewById(R.id.text_like_count_post);
            comments      = view.findViewById(R.id.text_comment_count_post);
            avatar        = view.findViewById(R.id.avatar_post);
            seeMoreButton = view.findViewById(R.id.seemore_post);
            optionsButton = view.findViewById(R.id.options_icon_post);
            likeButton    = view.findViewById(R.id.like_button_post);

            optionsButton.setOnClickListener(this);
        }
        void bind(Result post){
            String address = post.getUser().getCity() + ", " + post.getUser().getCountry();

            name.setText(post.getUser().getFullName());
            location.setText(address);
            text.setText(post.getText());

            //see more button toggle for large texts
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (text.getLayout() != null) {
                        Log.d(TAG, "line count = " + text.getLayout().getLineCount());
                        if (text.getLayout().getLineCount() > 9)
                            seeMoreButton.setVisibility(View.VISIBLE);
                        else
                            seeMoreButton.setVisibility(View.GONE);
                    }
                }
            },100);

            if(post.getCommentCount() >1) {
                String commentText = post.getCommentCount() + " comments";
                comments.setText(commentText);
            }else{
                String commentText = post.getCommentCount() + " comment";
                comments.setText(commentText);
            }

            if(post.getLikeCount() >1) {
                String likeText = post.getLikeCount() + " likes";
                likes.setText(likeText);
            }else{
                String likeText = post.getLikeCount() + " like";
                likes.setText(likeText);
            }

            String profilePhotoUrl = "http://167.99.13.238:8000" + post.getUser().getProfileImgUrl();
            Picasso
                    .get()
                    .load(profilePhotoUrl)
                    .into(avatar);
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            if(view.getId() == R.id.options_icon_post)
                onOptionsButtonClickListener.onOptionsButtonClick(position);
            if(view.getId() == R.id.like_button_post)
                onLikeButtonClickListener.onLikeButtonClick(position);
        }
    }

    //Image view holder class
    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location, text, likes, comments, seeMoreButton;
        public CircleImageView avatar;
        public AspectRatioImageView landscapeImageView, portraitImageView;
        private ImageButton optionsButton;

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
            seeMoreButton      = view.findViewById(R.id.seemore_post);
            optionsButton      = view.findViewById(R.id.options_icon_post);

            optionsButton.setOnClickListener(this);
        }
        void bind(Result post){
            String address = post.getUser().getCity() + ", " + post.getUser().getCountry();

            name.setText(post.getUser().getFullName());
            location.setText(address);
            text.setText(post.getText());

            //see more button toggle for large texts
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (text.getLayout() != null) {
                        Log.d(TAG, "line count = " + text.getLayout().getLineCount());
                        if (text.getLayout().getLineCount() > 9)
                            seeMoreButton.setVisibility(View.VISIBLE);
                        else
                            seeMoreButton.setVisibility(View.GONE);
                    }
                }
            },100);

            String imageUrl = "http://167.99.13.238:8000" + post.getFiles().get(0).getMedia();

            //Set comment count on post
            if(post.getCommentCount() >1) {
                String commentText = post.getCommentCount() + " comments";
                comments.setText(commentText);
            }else{
                String commentText = post.getCommentCount() + " comment";
                comments.setText(commentText);
            }
            if(post.getLikeCount() >1) {
                String likeText = post.getLikeCount() + " likes";
                likes.setText(likeText);
            }else{
                String likeText = post.getLikeCount() + " like";
                likes.setText(likeText);
            }

            String profilePhotoUrl = "http://167.99.13.238:8000" + post.getUser().getProfileImgUrl();
            Picasso
                    .get()
                    .load(profilePhotoUrl)
                    .into(avatar);

            //Placing image into respective imageview based on aspect ratio
            if (post.getFiles().get(0).getAspect().equals("portrait")){
                landscapeImageView.setVisibility(View.GONE);
                portraitImageView.setVisibility(View.VISIBLE);
                Picasso
                        .get()
                        .load(imageUrl)
                        .into(portraitImageView);
            }else{
                landscapeImageView.setVisibility(View.VISIBLE);
                portraitImageView.setVisibility(View.GONE);
                Picasso
                        .get()
                        .load(imageUrl)
                        .into(landscapeImageView);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            onOptionsButtonClickListener.onOptionsButtonClick(position);
        }
    }

    //Video view holder class
    private class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location, text, likes, comments, seeMoreButton;
        public CircleImageView avatar;
        private AndExoPlayerView videoPlayerPortrait, videoPlayerLandscape;
        private ImageButton optionsButton;

        public VideoViewHolder(@NonNull View view) {
            super(view);

            name                 = view.findViewById(R.id.name_post);
            location             = view.findViewById(R.id.location_post);
            text                 = view.findViewById(R.id.text_post);
            likes                = view.findViewById(R.id.text_like_count_post);
            comments             = view.findViewById(R.id.text_comment_count_post);
            avatar               = view.findViewById(R.id.avatar_post);
            seeMoreButton        = view.findViewById(R.id.seemore_post);
            videoPlayerPortrait  = view.findViewById(R.id.video_player_portrait_post);
            videoPlayerLandscape = view.findViewById(R.id.video_player_landscape_post);
            optionsButton        = view.findViewById(R.id.options_icon_post);

            optionsButton.setOnClickListener(this);
        }
        void bind(Result post){
            String address = post.getUser().getCity() + ", " + post.getUser().getCountry();

            name.setText(post.getUser().getFullName());
            location.setText(address);
            text.setText(post.getText());

            //see more button toggle for large texts
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (text.getLayout() != null) {
                        Log.d(TAG, "line count = " + text.getLayout().getLineCount());
                        if (text.getLayout().getLineCount() > 9)
                            seeMoreButton.setVisibility(View.VISIBLE);
                        else
                            seeMoreButton.setVisibility(View.GONE);
                    }
                }
            },100);

            //Set comment count on post
            if(post.getCommentCount() >1) {
                String commentText = post.getCommentCount() + " comments";
                comments.setText(commentText);
            }else{
                String commentText = post.getCommentCount() + " comment";
                comments.setText(commentText);
            }
            if(post.getLikeCount() >1) {
                String likeText = post.getLikeCount() + " likes";
                likes.setText(likeText);
            }else{
                String likeText = post.getLikeCount() + " like";
                likes.setText(likeText);
            }

            String profilePhotoUrl = "http://167.99.13.238:8000" + post.getUser().getProfileImgUrl();
            Picasso
                    .get()
                    .load(profilePhotoUrl)
                    .into(avatar);

            String videoUrl = "http://167.99.13.238:8000" + post.getFiles().get(0).getMedia();

//            //Set MediaController  to enable play, pause, forward, etc options.
//            MediaController mediaController = new MediaController(context);
//
//            //TODO: later toggle the imageview based on image aspect ratio
//            landscapeVideoView.setVisibility(View.GONE);
//            portraitVideoView.setVisibility(View.VISIBLE);
//
//            mediaController.setAnchorView(portraitVideoView);
//            portraitVideoView.setMediaController(mediaController);
//
//            portraitVideoView.setVideoPath(videoUrl);
//            portraitVideoView.requestFocus();
            if (post.getFiles().get(0).getAspect().equals("portrait")){
                videoPlayerPortrait.setVisibility(View.VISIBLE);
                videoPlayerLandscape.setVisibility(View.GONE);
                videoPlayerPortrait.setSource(videoUrl);
            }else{
                videoPlayerLandscape.setVisibility(View.VISIBLE);
                videoPlayerPortrait.setVisibility(View.GONE);
                videoPlayerLandscape.setSource(videoUrl);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            onOptionsButtonClickListener.onOptionsButtonClick(position);
        }
    }

    //Loading view holder class
    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);

            progressBar = view.findViewById(R.id.progressbar);
        }
        void bind(){
            progressBar.setIndeterminate(true);
        }
    }
}