package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Fragments.ProfileSectionFragment;
import hn.techcom.com.hncitipages.Interfaces.OnCommentClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnFavoriteButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLikeCountButtonListener;
import hn.techcom.com.hncitipages.Interfaces.OnOptionsButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnPlayerPlayedListener;
import hn.techcom.com.hncitipages.Interfaces.OnPostCountClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnSupporterSupportingCountClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnUpdateProfileClickListener;
import hn.techcom.com.hncitipages.Interfaces.ViewProfileListener;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.Result;
import hn.techcom.com.hncitipages.Models.SingleUserInfoResponse;
import hn.techcom.com.hncitipages.Models.SupportingProfileList;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class SupportingPostAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<Result> allPosts;
    private SupportingProfileList allProfiles;

    //Constants
    private static final int VIEW_TYPE_PROFILE = -1;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_STORY   = 1;
    private static final int VIEW_TYPE_IMAGE   = 2;
    private static final int VIEW_TYPE_VIDEO   = 3;
    private static final int VIEW_TYPE_AUDIO   = 4;

    private static final String TAG = "SupportingPostAdapter";

    //instance of interface
    private final OnOptionsButtonClickListener onOptionsButtonClickListener;
    private final OnLikeButtonClickListener onLikeButtonClickListener;
    private final OnFavoriteButtonClickListener onFavoriteButtonClickListener;
    private final OnLikeCountButtonListener onLikeCountButtonListener;
    private final OnCommentClickListener onCommentClickListener;
    private final OnPlayerPlayedListener onPlayerPlayedListener;
    private ViewProfileListener viewProfileListener;

    private Utils myUtils;
    private final Profile userProfile;

    public SupportingPostAdapter(
            Context context,
            ArrayList<Result> allPosts,
            SupportingProfileList allProfiles,
            OnOptionsButtonClickListener onOptionsButtonClickListener,
            OnLikeButtonClickListener onLikeButtonClickListener,
            OnFavoriteButtonClickListener onFavoriteButtonClickListener,
            OnLikeCountButtonListener onLikeCountButtonListener,
            OnCommentClickListener onCommentClickListener,
            OnPlayerPlayedListener onPlayerPlayedListener,
            ViewProfileListener viewProfileListener) {
        this.context = context;
        this.allPosts = allPosts;
        this.allProfiles = allProfiles;
        this.onOptionsButtonClickListener = onOptionsButtonClickListener;
        this.onLikeButtonClickListener = onLikeButtonClickListener;
        this.onFavoriteButtonClickListener = onFavoriteButtonClickListener;
        this.onLikeCountButtonListener = onLikeCountButtonListener;
        this.onCommentClickListener = onCommentClickListener;
        this.onPlayerPlayedListener = onPlayerPlayedListener;
        this.viewProfileListener = viewProfileListener;

        //getting user profile from local storage
        myUtils     = new Utils();
        //getting logged in user's profile
        userProfile = myUtils.getNewUserFromSharedPreference(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_STORY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_story_post, parent, false);
                return new SupportingPostAdapter.StoryViewHolder(view);

            case VIEW_TYPE_IMAGE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_image_post, parent, false);
                return new SupportingPostAdapter.ImageViewHolder(view);
            case VIEW_TYPE_VIDEO:
            case VIEW_TYPE_AUDIO:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_video_post, parent, false);
                return new SupportingPostAdapter.AudioVideoViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_loading_post, parent, false);
                return new SupportingPostAdapter.LoadingViewHolder(view);
            case VIEW_TYPE_PROFILE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_supporting_profiles, parent, false);
                return new SupportingPostAdapter.ProfileViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Result post = allPosts.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_STORY:
                ((SupportingPostAdapter.StoryViewHolder) holder).bind(post);
                break;
            case VIEW_TYPE_IMAGE:
                ((SupportingPostAdapter.ImageViewHolder) holder).bind(post);
                break;
            case VIEW_TYPE_VIDEO:
            case VIEW_TYPE_AUDIO:
                ((SupportingPostAdapter.AudioVideoViewHolder) holder).bind(post);
                break;
            case VIEW_TYPE_LOADING:
                ((SupportingPostAdapter.LoadingViewHolder) holder).bind();
                break;
            case VIEW_TYPE_PROFILE:
                ((SupportingPostAdapter.ProfileViewHolder) holder).bind();
                break;
        }
    }

    @Override
    public int getItemCount() {
        return allPosts == null ? 0 : allPosts.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_PROFILE;
        else {
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
        }
        return -2;
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

    public void filterList(ArrayList<Result> filterNames) {
        allPosts = filterNames;
        notifyDataSetChanged();
    }

    //View holder classes

    //Story view holder class
    private class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, title, location, text, likes, comments, seeMoreButton, time;
        public CircleImageView avatar;
        private ImageButton optionsButton, likeButton, favoriteButton, commentButton;
        private View supportCircleView;

        public StoryViewHolder(@NonNull View view) {
            super(view);

            name              = view.findViewById(R.id.name_post);
            title             = view.findViewById(R.id.title_post);
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
            time              = view.findViewById(R.id.time_post);

            optionsButton.setOnClickListener(this);
            likeButton.setOnClickListener(this);
            favoriteButton.setOnClickListener(this);
            likes.setOnClickListener(this);
            comments.setOnClickListener(this);
            commentButton.setOnClickListener(this);
            name.setOnClickListener(this);
            avatar.setOnClickListener(this);
        }
        void bind(Result post){
            String address = post.getUser().getCity() + ", " + post.getUser().getCountry();
            String user_title = post.getUser().getTitle();

            name.setText(post.getUser().getFullName());
            if (!user_title.equals("User")){
                title.setVisibility(View.VISIBLE);
                location.setVisibility(View.GONE);

                String user_title_text = user_title + ", HN CitiPages";
                title.setText(user_title_text);
            }else {
                if (post.getUser().getCity().equals("N/A") || post.getUser().getCountry().equals("N/A"))
                    location.setVisibility(View.GONE);
                else {
                    location.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    location.setText(address);
                }
            }
            text.setText(post.getText());
            time.setText(post.getCreatedOn());
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
            int count    = allPosts.get(position).getCommentCount();
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
                onCommentClickListener.onCommentClick(postId,count);
            if (view.getId() == R.id.name_post || view.getId() == R.id.avatar_post) {
                User user = allPosts.get(position).getUser();

                String hnid         = user.getHnid();
                String name         = user.getFullName();
                boolean isSupported = user.getIsSupported();

                viewProfileListener.viewProfile(hnid, name, isSupported);
            }
        }
    }

    //Image view holder class
    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, title, location, text, likes, comments, seeMoreButton, time;
        public CircleImageView avatar;
        public ImageView landscapeImageView;
        private ImageButton optionsButton, likeButton, favoriteButton, commentButton;
        private View supportCircleView;

        public ImageViewHolder(@NonNull View view) {
            super(view);

            name               = view.findViewById(R.id.name_post);
            title              = view.findViewById(R.id.title_post);
            location           = view.findViewById(R.id.location_post);
            text               = view.findViewById(R.id.text_post);
            likes              = view.findViewById(R.id.text_like_count_post);
            comments           = view.findViewById(R.id.text_comment_count_post);
            avatar             = view.findViewById(R.id.avatar_post);
            landscapeImageView = view.findViewById(R.id.imageview_landscape_post);

            seeMoreButton      = view.findViewById(R.id.seemore_post);
            optionsButton      = view.findViewById(R.id.options_icon_post);
            likeButton         = view.findViewById(R.id.like_button_post);
            favoriteButton     = view.findViewById(R.id.favorite_button_post);
            commentButton      = view.findViewById(R.id.comment_button_post);
            supportCircleView  = view.findViewById(R.id.support_circle_view);
            time               = view.findViewById(R.id.time_post);

            optionsButton.setOnClickListener(this);
            likeButton.setOnClickListener(this);
            favoriteButton.setOnClickListener(this);
            likes.setOnClickListener(this);
            comments.setOnClickListener(this);
            commentButton.setOnClickListener(this);
            name.setOnClickListener(this);
            avatar.setOnClickListener(this);
        }
        void bind(Result post){
            String address = post.getUser().getCity() + ", " + post.getUser().getCountry();
            String user_title = post.getUser().getTitle();

            name.setText(post.getUser().getFullName());
            if (!user_title.equals("User")){
                title.setVisibility(View.VISIBLE);
                location.setVisibility(View.GONE);

                String user_title_text = user_title + ", HN CitiPages";
                title.setText(user_title_text);
            }else {
                if (post.getUser().getCity().equals("N/A") || post.getUser().getCountry().equals("N/A"))
                    location.setVisibility(View.GONE);
                else {
                    location.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    location.setText(address);
                }
            }
            text.setText(post.getText());
            time.setText(post.getCreatedOn());
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

            Glide
                    .with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.landscape_image_placeholder)
                    .into(landscapeImageView);

            //Placing image into respective imageview based on aspect ratio
//            if (post.getFiles().get(0).getAspect().equals("portrait")){
//                landscapeImageView.setVisibility(View.GONE);
//                portraitImageView.setVisibility(View.VISIBLE);
////                Picasso
////                        .get()
////                        .load(imageUrl)
////                        .into(portraitImageView);
//
//                Glide.with(context).load(imageUrl).centerCrop().into(portraitImageView);
//            }
//            else{
//                landscapeImageView.setVisibility(View.VISIBLE);
//                portraitImageView.setVisibility(View.GONE);
////                Picasso
////                        .get()
////                        .load(imageUrl)
////                        .into(landscapeImageView);
//
//                Glide.with(context).load(imageUrl).centerCrop().into(landscapeImageView);
//            }
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            int postId   = allPosts.get(position).getId();
            int count    = allPosts.get(position).getCommentCount();
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
                onCommentClickListener.onCommentClick(postId,count);
            if (view.getId() == R.id.name_post || view.getId() == R.id.avatar_post) {
                User user = allPosts.get(position).getUser();

                String hnid         = user.getHnid();
                String name         = user.getFullName();
                boolean isSupported = user.getIsSupported();

                viewProfileListener.viewProfile(hnid, name, isSupported);
            }
        }
    }

    //Video view holder class
    private class AudioVideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, title, location, text, likes, comments, seeMoreButton, time;
        public CircleImageView avatar;
        private AndExoPlayerView videoPlayerPortrait, videoPlayerLandscape, audioPlayer;
        private ImageButton optionsButton, likeButton, favoriteButton, commentButton;
        private View supportCircleView;

        private ImageView imageviewLandscape, imageviewPortrait, playButtonLandscape, playButtonPortrait, playButtonAudio;
        private RelativeLayout videoLandscapeLayout, videoPortraitLayout, audioPlayerLayout;

        public AudioVideoViewHolder(@NonNull View view) {
            super(view);

            name                 = view.findViewById(R.id.name_post);
            title                = view.findViewById(R.id.title_post);
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
            time                 = view.findViewById(R.id.time_post);
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

            name.setOnClickListener(this);
            avatar.setOnClickListener(this);
        }
        void bind(Result post){
            String address = post.getUser().getCity() + ", " + post.getUser().getCountry();
            String user_title = post.getUser().getTitle();

            name.setText(post.getUser().getFullName());
            if (!user_title.equals("User")){
                title.setVisibility(View.VISIBLE);
                location.setVisibility(View.GONE);

                String user_title_text = user_title + ", HN CitiPages";
                title.setText(user_title_text);
            }else {
                if (post.getUser().getCity().equals("N/A") || post.getUser().getCountry().equals("N/A"))
                    location.setVisibility(View.GONE);
                else {
                    location.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    location.setText(address);
                }
            }
            text.setText(post.getText());
            time.setText(post.getCreatedOn());
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
            int count    = allPosts.get(position).getCommentCount();
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
                onCommentClickListener.onCommentClick(postId,count);
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
            if (view.getId() == R.id.name_post || view.getId() == R.id.avatar_post) {
                User user = allPosts.get(position).getUser();

                String hnid         = user.getHnid();
                String name         = user.getFullName();
                boolean isSupported = user.getIsSupported();

                viewProfileListener.viewProfile(hnid, name, isSupported);
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

    //Profile view holder class
    private class ProfileViewHolder extends RecyclerView.ViewHolder
            implements ViewProfileListener {

        private RecyclerView recyclerView;

        public ProfileViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.recyclerview);
        }

        void bind(){
            Log.d(TAG,"allProfiles number = "+allProfiles.getResults().size());
            AvatarLoaderAdapter avatarLoaderAdapter = new AvatarLoaderAdapter(
                    context, allProfiles, this
            );
            // Set Horizontal Layout Manager
            // for Recycler view
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false);
            recyclerView.setLayoutManager(linearLayoutManager);

            // Set adapter on recycler view
            recyclerView.setAdapter(avatarLoaderAdapter);
        }

        @Override
        public void viewProfile(String hnid, String name, boolean isSupported) {
            Fragment fragment = new ProfileSectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("hnid",hnid);
            bundle.putString("name",name);
            bundle.putBoolean("isSupported",isSupported);

            fragment.setArguments(bundle);
            ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
        }
    }

    public void changeSupportingStatus(int position){
        allPosts.get(position).getUser().setIsSupported(!allPosts.get(position).getUser().getIsSupported());
        notifyDataSetChanged();
    }
}
