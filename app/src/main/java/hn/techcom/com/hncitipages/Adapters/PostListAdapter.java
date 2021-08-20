package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;
import com.santalu.aspectratioimageview.AspectRatioImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Interfaces.OnCommentClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnFavoriteButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeCountButtonListener;
import hn.techcom.com.hncitipages.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hncitipages.Interfaces.OnOptionsButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnPlayerPlayedListener;
import hn.techcom.com.hncitipages.Models.Result;
import hn.techcom.com.hncitipages.R;

public class PostListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //Constants
    private static final String TAG = "PostListAdapter";
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_STORY = 1;
    private static final int VIEW_TYPE_IMAGE = 2;
    private static final int VIEW_TYPE_VIDEO = 3;
    private static final int VIEW_TYPE_AUDIO = 4;


    private RecyclerView recyclerView;
    private  ArrayList<Result> allPosts = new ArrayList<>();

    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private Context context;

    //instance of interface
    private final OnOptionsButtonClickListener onOptionsButtonClickListener;
    private final OnLikeButtonClickListener onLikeButtonClickListener;
    private final OnFavoriteButtonClickListener onFavoriteButtonClickListener;
    private final OnLikeCountButtonListener onLikeCountButtonListener;
    private final OnCommentClickListener onCommentClickListener;
    private final OnLoadMoreListener onLoadMoreListener;

    private final OnPlayerPlayedListener onPlayerPlayedListener;

    public PostListAdapter(
            ArrayList<Result> allPosts,
            Context context,
            OnOptionsButtonClickListener onOptionsButtonClickListener,
            OnLikeButtonClickListener onLikeButtonClickListener,
            OnFavoriteButtonClickListener onFavoriteButtonClickListener,
            OnLikeCountButtonListener onLikeCountButtonListener,
            OnCommentClickListener onCommentClickListener,
            OnLoadMoreListener onLoadMoreListener,
            OnPlayerPlayedListener onPlayerPlayedListener)
    {
        this.allPosts = allPosts;
        this.context = context;
        this.onOptionsButtonClickListener = onOptionsButtonClickListener;
        this.onLikeButtonClickListener = onLikeButtonClickListener;
        this.onFavoriteButtonClickListener = onFavoriteButtonClickListener;
        this.onLikeCountButtonListener = onLikeCountButtonListener;
        this.onCommentClickListener = onCommentClickListener;
        this.onLoadMoreListener = onLoadMoreListener;
        this.onPlayerPlayedListener = onPlayerPlayedListener;

        Log.d(TAG,"post list size in adapter = "+allPosts.size());
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
            case VIEW_TYPE_AUDIO:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_video_post, parent, false);
                return new AudioVideoViewHolder(view);
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
            case VIEW_TYPE_AUDIO:
                ((AudioVideoViewHolder) holder).bind(post);
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

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        AndExoPlayerView portrait = holder.itemView.findViewById(R.id.video_player_portrait_post);
        AndExoPlayerView landscapre = holder.itemView.findViewById(R.id.video_player_landscape_post);
        if (portrait != null)
            portrait.stopPlayer();
        else if(landscapre != null)
            landscapre.stopPlayer();
    }
//View holder classes

    //Story view holder class
    private class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location, text, likes, comments, seeMoreButton;
        public CircleImageView avatar;
        private ImageButton optionsButton, likeButton, favoriteButton, commentButton;
        private View supportCircleView;

        public StoryViewHolder(@NonNull View view) {
            super(view);

            name              = view.findViewById(R.id.name_post);
            location          = view.findViewById(R.id.location_post);
            text              = view.findViewById(R.id.text_post);
            likes             = view.findViewById(R.id.text_like_count_post);
            comments          = view.findViewById(R.id.text_comment_count_post);
            avatar            = view.findViewById(R.id.avatar_post);
            seeMoreButton     = view.findViewById(R.id.seemore_post);
            optionsButton     = view.findViewById(R.id.options_icon_post);
            likeButton        = view.findViewById(R.id.like_button_post);
            favoriteButton    = view.findViewById(R.id.favorite_button_post);
            commentButton     = view.findViewById(R.id.comment_button_post);
            supportCircleView = view.findViewById(R.id.support_circle_view);

            optionsButton.setOnClickListener(this);
            likeButton.setOnClickListener(this);
            favoriteButton.setOnClickListener(this);
            likes.setOnClickListener(this);
            comments.setOnClickListener(this);
            commentButton.setOnClickListener(this);
        }
        void bind(Result post){
            String address = post.getUser().getCity() + ", " + post.getUser().getCountry();
            name.setText(post.getUser().getFullName());
            if (post.getUser().getCity().equals("N/A") || post.getUser().getCountry().equals("N/A"))
                location.setVisibility(View.GONE);
            else
                location.setText(address);
            text.setText(post.getText());

            //see more button toggle for large texts
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (text.getLayout() != null) {
//                        Log.d(TAG, "line count = " + text.getLayout().getLineCount());
//                        if (text.getLayout().getLineCount() > 9)
//                            seeMoreButton.setVisibility(View.VISIBLE);
//                        else
//                            seeMoreButton.setVisibility(View.GONE);
//                    }
//                }
//            },100);

            //Toggling like button
            if(post.getLiked())
                likeButton.setImageResource(R.drawable.filled_thumb_up_24_ic);
            else
                likeButton.setImageResource(R.drawable.outline_thumb_up_24_ic);

            //Toggling favorite button
            if(post.getFavourite())
                favoriteButton.setImageResource(R.drawable.favorite_ic_selected);
            else
                favoriteButton.setImageResource(R.drawable.outline_favorite_24_ic);

            Log.d(TAG, "favorite = "+post.getFavourite().toString());

            //Toggling comment count text
            if (post.getCommentCount() != 0){
                comments.setVisibility(View.VISIBLE);
                if(post.getCommentCount() >1) {
                    String commentText = post.getCommentCount() + " comments";
                    comments.setText(commentText);
                }else{
                    String commentText = post.getCommentCount() + " comment";
                    comments.setText(commentText);
                }
            }else
                comments.setVisibility(View.GONE);

            //Toggling like count text
            if(post.getLikeCount() != 0){
                likes.setVisibility(View.VISIBLE);
                if(post.getLikeCount() >1) {
                    String likeText = post.getLikeCount() + " likes";
                    likes.setText(likeText);
                }else{
                    String likeText = post.getLikeCount() + " like";
                    likes.setText(likeText);
                }
            }else
                likes.setVisibility(View.GONE);

            String profilePhotoUrl = post.getUser().getProfileImgThumbnail();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(avatar);

            Glide.with(context).load(profilePhotoUrl).centerCrop().into(avatar);

            //Toggling support avatar circle
            if(post.getUser().getIsSupported())
                supportCircleView.setVisibility(View.VISIBLE);
            else
                supportCircleView.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            int postId   = allPosts.get(position).getId();
            String hnid_user = allPosts.get(position).getUser().getHnid();
            boolean supporting = allPosts.get(position).getUser().getIsSupported();
            if(view.getId() == R.id.options_icon_post)
                onOptionsButtonClickListener.onOptionsButtonClick(position, postId, hnid_user, supporting);
            if(view.getId() == R.id.like_button_post)
                onLikeButtonClickListener.onLikeButtonClick(position, postId);
            if(view.getId() == R.id.favorite_button_post)
                onFavoriteButtonClickListener.onFavoriteButtonClick(position, postId);
            if(view.getId() == R.id.text_like_count_post)
                onLikeCountButtonListener.onLikeCountButtonClick(postId);
            if(view.getId() == R.id.comment_button_post || view.getId() == R.id.text_comment_count_post)
                onCommentClickListener.onCommentClick(postId);
        }
    }

    //Image view holder class
    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location, text, likes, comments, seeMoreButton;
        public CircleImageView avatar;
        public AspectRatioImageView landscapeImageView, portraitImageView;
        private ImageButton optionsButton, likeButton, favoriteButton, commentButton;
        private View supportCircleView;

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
            likeButton         = view.findViewById(R.id.like_button_post);
            favoriteButton     = view.findViewById(R.id.favorite_button_post);
            commentButton      = view.findViewById(R.id.comment_button_post);
            supportCircleView  = view.findViewById(R.id.support_circle_view);

            optionsButton.setOnClickListener(this);
            likeButton.setOnClickListener(this);
            favoriteButton.setOnClickListener(this);
            likes.setOnClickListener(this);
            comments.setOnClickListener(this);
            commentButton.setOnClickListener(this);
        }
        void bind(Result post){
            String address = post.getUser().getCity() + ", " + post.getUser().getCountry();

            name.setText(post.getUser().getFullName());
            if (post.getUser().getCity().equals("N/A") || post.getUser().getCountry().equals("N/A"))
                location.setVisibility(View.GONE);
            else
                location.setText(address);
            text.setText(post.getText());

            //see more button toggle for large texts
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (text.getLayout() != null) {
//                        Log.d(TAG, "line count = " + text.getLayout().getLineCount());
//                        if (text.getLayout().getLineCount() > 9)
//                            seeMoreButton.setVisibility(View.VISIBLE);
//                        else
//                            seeMoreButton.setVisibility(View.GONE);
//                    }
//                }
//            },100);

            String imageUrl = post.getFiles().get(0).getMedia();

            //Toggling like button
            if(post.getLiked())
                likeButton.setImageResource(R.drawable.filled_thumb_up_24_ic);
            else
                likeButton.setImageResource(R.drawable.outline_thumb_up_24_ic);

            //Toggling favorite button
            if(post.getFavourite())
                favoriteButton.setImageResource(R.drawable.favorite_ic_selected);
            else
                favoriteButton.setImageResource(R.drawable.outline_favorite_24_ic);

            Log.d(TAG, "favorite = "+post.getFavourite().toString());

            //Toggling comment count text
            if (post.getCommentCount() != 0){
                comments.setVisibility(View.VISIBLE);
                if(post.getCommentCount() >1) {
                    String commentText = post.getCommentCount() + " comments";
                    comments.setText(commentText);
                }else{
                    String commentText = post.getCommentCount() + " comment";
                    comments.setText(commentText);
                }
            }else
                comments.setVisibility(View.GONE);

            //Toggling like count text
            if(post.getLikeCount() != 0){
                likes.setVisibility(View.VISIBLE);
                if(post.getLikeCount() >1) {
                    String likeText = post.getLikeCount() + " likes";
                    likes.setText(likeText);
                }else{
                    String likeText = post.getLikeCount() + " like";
                    likes.setText(likeText);
                }
            }else
                likes.setVisibility(View.GONE);

            String profilePhotoUrl = post.getUser().getProfileImgThumbnail();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(avatar);

            Glide.with(context).load(profilePhotoUrl).centerCrop().into(avatar);

            //Toggling support avatar circle
            if(post.getUser().getIsSupported())
                supportCircleView.setVisibility(View.VISIBLE);
            else
                supportCircleView.setVisibility(View.GONE);

            //Placing image into respective imageview based on aspect ratio
            if (post.getFiles().get(0).getAspect().equals("portrait")){
                landscapeImageView.setVisibility(View.GONE);
                portraitImageView.setVisibility(View.VISIBLE);
//                Picasso
//                        .get()
//                        .load(imageUrl)
//                        .into(portraitImageView);

                Glide.with(context).load(imageUrl).centerCrop().into(portraitImageView);
            }else{
                landscapeImageView.setVisibility(View.VISIBLE);
                portraitImageView.setVisibility(View.GONE);
//                Picasso
//                        .get()
//                        .load(imageUrl)
//                        .into(landscapeImageView);

                Glide.with(context).load(imageUrl).centerCrop().into(landscapeImageView);
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            int postId   = allPosts.get(position).getId();
            String hnid_user = allPosts.get(position).getUser().getHnid();
            boolean supporting = allPosts.get(position).getUser().getIsSupported();
            if(view.getId() == R.id.options_icon_post)
                onOptionsButtonClickListener.onOptionsButtonClick(position, postId, hnid_user, supporting);
            if(view.getId() == R.id.like_button_post)
                onLikeButtonClickListener.onLikeButtonClick(position, postId);
            if(view.getId() == R.id.favorite_button_post)
                onFavoriteButtonClickListener.onFavoriteButtonClick(position, postId);
            if(view.getId() == R.id.text_like_count_post)
                onLikeCountButtonListener.onLikeCountButtonClick(postId);
            if(view.getId() == R.id.comment_button_post || view.getId() == R.id.text_comment_count_post)
                onCommentClickListener.onCommentClick(postId);
        }
    }

    //Video view holder class
    private class AudioVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location, text, likes, comments, seeMoreButton;
        public CircleImageView avatar;
        private AndExoPlayerView videoPlayerPortrait, videoPlayerLandscape, audioPlayer;
        private ImageButton optionsButton, likeButton, favoriteButton, commentButton;
        private View supportCircleView;

        private ImageView imageviewLandscape, imageviewPortrait, playButtonLandscape, playButtonPortrait, playButtonAudio;
        private RelativeLayout videoLandscapeLayout, videoPortraitLayout, audioPlayerLayout;

        public AudioVideoViewHolder(@NonNull View view) {
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
            audioPlayer          = view.findViewById(R.id.audio_player);
            audioPlayerLayout    = view.findViewById(R.id.audio_player_layout);
            optionsButton        = view.findViewById(R.id.options_icon_post);
            likeButton           = view.findViewById(R.id.like_button_post);
            favoriteButton       = view.findViewById(R.id.favorite_button_post);
            commentButton        = view.findViewById(R.id.comment_button_post);
            supportCircleView    = view.findViewById(R.id.support_circle_view);

            imageviewLandscape   = view.findViewById(R.id.imageview_video_landscape);
            imageviewPortrait    = view.findViewById(R.id.imageview_video_portrait);

            playButtonAudio      = view.findViewById(R.id.play_button_audio);

            playButtonLandscape  = view.findViewById(R.id.play_button_landscape);
            videoLandscapeLayout = view.findViewById(R.id.layout_video_landscape);

            playButtonPortrait   = view.findViewById(R.id.play_button_portrait);
            videoPortraitLayout  = view.findViewById(R.id.layout_video_portrait);

            optionsButton.setOnClickListener(this);
            likeButton.setOnClickListener(this);


            favoriteButton.setOnClickListener(this);
            likes.setOnClickListener(this);
            comments.setOnClickListener(this);
            commentButton.setOnClickListener(this);

            playButtonLandscape.setOnClickListener(this);
            playButtonPortrait.setOnClickListener(this);
            playButtonAudio.setOnClickListener(this);
        }
        void bind(Result post){
            String address = post.getUser().getCity() + ", " + post.getUser().getCountry();

            name.setText(post.getUser().getFullName());
            if (post.getUser().getCity().equals("N/A") || post.getUser().getCountry().equals("N/A"))
                location.setVisibility(View.GONE);
            else
                location.setText(address);
            text.setText(post.getText());

            //see more button toggle for large texts
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (text.getLayout() != null) {
//                        Log.d(TAG, "line count = " + text.getLayout().getLineCount());
//                        if (text.getLayout().getLineCount() > 9)
//                            seeMoreButton.setVisibility(View.VISIBLE);
//                        else
//                            seeMoreButton.setVisibility(View.GONE);
//                    }
//                }
//            },100);

            //Toggling like button
            if(post.getLiked())
                likeButton.setImageResource(R.drawable.filled_thumb_up_24_ic);
            else
                likeButton.setImageResource(R.drawable.outline_thumb_up_24_ic);

            //Toggling favorite button
            if(post.getFavourite())
                favoriteButton.setImageResource(R.drawable.favorite_ic_selected);
            else
                favoriteButton.setImageResource(R.drawable.outline_favorite_24_ic);

            Log.d(TAG, "favorite = "+post.getFavourite().toString());


            //Toggling comment count text
            if (post.getCommentCount() != 0){
                comments.setVisibility(View.VISIBLE);
                if(post.getCommentCount() >1) {
                    String commentText = post.getCommentCount() + " comments";
                    comments.setText(commentText);
                }else{
                    String commentText = post.getCommentCount() + " comment";
                    comments.setText(commentText);
                }
            }else
                comments.setVisibility(View.GONE);

            //Toggling like count text
            if(post.getLikeCount() != 0){
                likes.setVisibility(View.VISIBLE);
                if(post.getLikeCount() >1) {
                    String likeText = post.getLikeCount() + " likes";
                    likes.setText(likeText);
                }else{
                    String likeText = post.getLikeCount() + " like";
                    likes.setText(likeText);
                }
            }else
                likes.setVisibility(View.GONE);

            String profilePhotoUrl = post.getUser().getProfileImgThumbnail();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(avatar);

            Glide.with(context).load(profilePhotoUrl).centerCrop().into(avatar);

            //Toggling support avatar circle
            if(post.getUser().getIsSupported())
                supportCircleView.setVisibility(View.VISIBLE);
            else
                supportCircleView.setVisibility(View.GONE);

            String videoUrl = post.getFiles().get(0).getMedia();

            if(post.getPosttype().equals("A")){
                videoPlayerLandscape.setVisibility(View.GONE);
                videoPlayerPortrait.setVisibility(View.GONE);
                audioPlayerLayout.setVisibility(View.VISIBLE);
                audioPlayer.stopPlayer();
                audioPlayer.setVisibility(View.GONE);
                playButtonAudio.setVisibility(View.VISIBLE);
//                audioPlayer.setSource(videoUrl);
//                audioPlayer.setPlayWhenReady(false);
            }else{
                if (post.getFiles().get(0).getAspect().equals("portrait")){
                    videoLandscapeLayout.setVisibility(View.GONE);
                    videoPortraitLayout.setVisibility(View.VISIBLE);
                    imageviewPortrait.setVisibility(View.VISIBLE);
                    if (post.getFiles().get(0).getThumbnail() != null) {
                        Glide.with(context)
                                .load(post.getFiles().get(0).getThumbnail())
                                .into(imageviewPortrait);
                    }
                    videoPlayerPortrait.stopPlayer();
                    playButtonPortrait.setVisibility(View.VISIBLE);
                }else{
                    videoPortraitLayout.setVisibility(View.GONE);
                    videoLandscapeLayout.setVisibility(View.VISIBLE);
                    imageviewLandscape.setVisibility(View.VISIBLE);
                    if (post.getFiles().get(0).getThumbnail() != null) {
                        Glide.with(context)
                                .load(post.getFiles().get(0).getThumbnail())
                                .into(imageviewLandscape);
                    }
                    videoPlayerLandscape.stopPlayer();
                    playButtonLandscape.setVisibility(View.VISIBLE);

//                    Glide.with(context)
//                            .load(videoUrl)
//                            .into(imageviewLandscape);
                }
            }
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            int postId   = allPosts.get(position).getId();
            String hnid_user = allPosts.get(position).getUser().getHnid();
            boolean supporting = allPosts.get(position).getUser().getIsSupported();
            if(view.getId() == R.id.options_icon_post)
                onOptionsButtonClickListener.onOptionsButtonClick(position, postId, hnid_user, supporting);
            if(view.getId() == R.id.like_button_post)
                onLikeButtonClickListener.onLikeButtonClick(position, postId);
            if(view.getId() == R.id.favorite_button_post)
                onFavoriteButtonClickListener.onFavoriteButtonClick(position, postId);
            if(view.getId() == R.id.text_like_count_post)
                onLikeCountButtonListener.onLikeCountButtonClick(postId);
            if(view.getId() == R.id.comment_button_post || view.getId() == R.id.text_comment_count_post)
                onCommentClickListener.onCommentClick(postId);
            if (view.getId() == R.id.play_button_landscape){
                String videoUrl = allPosts.get(position).getFiles().get(0).getMedia();
                playButtonLandscape.setVisibility(View.GONE);
                imageviewLandscape.setVisibility(View.GONE);
                videoPlayerLandscape.setVisibility(View.VISIBLE);
                videoPlayerLandscape.setSource(videoUrl);
                videoPlayerLandscape.setPlayWhenReady(true);
                onPlayerPlayedListener.onPlayerPlayed(videoPlayerLandscape, imageviewLandscape, playButtonLandscape);
            }
            if (view.getId() == R.id.play_button_portrait){
                String videoUrl = allPosts.get(position).getFiles().get(0).getMedia();
                playButtonPortrait.setVisibility(View.GONE);
                imageviewPortrait.setVisibility(View.GONE);
                videoPlayerPortrait.setVisibility(View.VISIBLE);
                videoPlayerPortrait.setSource(videoUrl);
                videoPlayerPortrait.setPlayWhenReady(true);
                onPlayerPlayedListener.onPlayerPlayed(videoPlayerPortrait, imageviewPortrait, playButtonPortrait);
            }
            if (view.getId() == R.id.play_button_audio){
                String audioUrl = allPosts.get(position).getFiles().get(0).getMedia();
                playButtonAudio.setVisibility(View.GONE);
                audioPlayer.setVisibility(View.VISIBLE);
                audioPlayer.setSource(audioUrl);
                audioPlayer.setPlayWhenReady(true);
                onPlayerPlayedListener.onPlayerPlayed(audioPlayer, null, playButtonAudio);
            }
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

    public void removeItem(int position) {
        allPosts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, getItemCount() - position);
    }

    public void changeSupportingStatus(int position){
        allPosts.get(position).getUser().setIsSupported(!allPosts.get(position).getUser().getIsSupported());
        notifyDataSetChanged();
    }


    //This method will filter the list
    //here we are passing the filtered data
    //and assigning it to the list with notifydatasetchanged method
    public void filterList(ArrayList<Result> filterdNames) {
        allPosts = filterdNames;
        notifyDataSetChanged();
    }

}