package hn.techcom.com.hncitipages.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;
import com.potyvideo.library.AndExoPlayerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Fragments.LikesFragment;
import hn.techcom.com.hncitipages.Interfaces.GetDataService;
import hn.techcom.com.hncitipages.Models.Result;
import hn.techcom.com.hncitipages.Network.RetrofitClientInstance;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationPostActivity extends AppCompatActivity implements View.OnClickListener{

    private View supportCircleView;
    private CircleImageView userAvatarView;
    private MaterialTextView userFullnameText, userLocationText, postTimeText, postText, likeCountText, commentCountText;
    private ImageView postImageView, playButtonLandscape, playButtonPortrait, playButtonAudio, imageviewLandscape, imageviewPortrait;
    private LinearLayout dataLinearLayout, shimmerLinearLayout;
    private RelativeLayout videoLandscapeLayout, videoPortraitLayout, audioPlayerLayout;
    private AndExoPlayerView videoPlayerPortrait, videoPlayerLandscape, audioPlayer;
    private Result post;
    private int postId;
    private static final String TAG = "NotificationPostActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_post);

        //Hooks
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title_view_comments);
        ImageButton backButton       = findViewById(R.id.image_button_back);

        dataLinearLayout             = findViewById(R.id.dataLayout_notification_post);
        shimmerLinearLayout          = findViewById(R.id.shimmerLayout_notification_post);

        supportCircleView    = findViewById(R.id.support_circle_view_notification_post);
        userAvatarView       = findViewById(R.id.avatar_notification_post);
        userFullnameText     = findViewById(R.id.name_notification_post);
        userLocationText     = findViewById(R.id.location_notification_post);
        postTimeText         = findViewById(R.id.time_notification_post);
        postText             = findViewById(R.id.text_notification_post);
        likeCountText        = findViewById(R.id.text_like_count__notification_post);
        commentCountText     = findViewById(R.id.text_comment_count_post);

        //for image posts
        postImageView        = findViewById(R.id.imageview_notification_post);

        //for landscape video
        videoLandscapeLayout = findViewById(R.id.layout_video_landscape);
        playButtonLandscape  = findViewById(R.id.play_button_landscape);
        imageviewLandscape   = findViewById(R.id.imageview_video_landscape);
        videoPlayerLandscape = findViewById(R.id.video_player_landscape_post);

        //for portrait video
        videoPortraitLayout  = findViewById(R.id.layout_video_portrait);
        playButtonPortrait   = findViewById(R.id.play_button_portrait);
        imageviewPortrait    = findViewById(R.id.imageview_video_portrait);
        videoPlayerPortrait  = findViewById(R.id.video_player_portrait_post);

        //for audio post
        audioPlayerLayout    = findViewById(R.id.audio_player_layout);
        audioPlayer          = findViewById(R.id.audio_player);
        playButtonAudio      = findViewById(R.id.play_button_audio);

        String senderName   = getIntent().getStringExtra("sender_name");
        postId              = getIntent().getIntExtra("postId",-1);
        String type         = getIntent().getStringExtra("type");
        boolean isSupported = getIntent().getBooleanExtra("isSupported",false);

        switch (type){
            case "L":
                String titleText = senderName+" Liked";
                screenTitle.setText(titleText);
                break;
        }

        if (isSupported)
            supportCircleView.setVisibility(View.VISIBLE);

        getPost(postId);

        //OnClick Listeners
        backButton.setOnClickListener(this);
        likeCountText.setOnClickListener(this);
        commentCountText.setOnClickListener(this);
        playButtonLandscape.setOnClickListener(this);
        playButtonPortrait.setOnClickListener(this);
        playButtonAudio.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoPlayerLandscape.stopPlayer();
        videoPlayerPortrait.stopPlayer();
        audioPlayer.stopPlayer();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_button_back)
            startActivity(new Intent(this,MainActivity.class));
        if (v.getId() == R.id.play_button_landscape){
            String videoUrl = post.getFiles().get(0).getMedia();
            playButtonLandscape.setVisibility(View.GONE);
            imageviewLandscape.setVisibility(View.GONE);
            videoPlayerLandscape.setVisibility(View.VISIBLE);
            videoPlayerLandscape.setSource(videoUrl);
            videoPlayerLandscape.setPlayWhenReady(true);
        }
        if (v.getId() == R.id.play_button_portrait){
            String videoUrl = post.getFiles().get(0).getMedia();
            playButtonPortrait.setVisibility(View.GONE);
            imageviewPortrait.setVisibility(View.GONE);
            videoPlayerPortrait.setVisibility(View.VISIBLE);
            videoPlayerPortrait.setSource(videoUrl);
            videoPlayerPortrait.setPlayWhenReady(true);
        }
        if (v.getId() == R.id.play_button_audio){
            String audioUrl = post.getFiles().get(0).getMedia();
            playButtonAudio.setVisibility(View.GONE);
            audioPlayer.setVisibility(View.VISIBLE);
            audioPlayer.setSource(audioUrl);
            audioPlayer.setPlayWhenReady(true);
        }
        if(v.getId() == R.id.text_like_count__notification_post){
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("from","notificationPostActivity");
            intent.putExtra("show","likes");
            intent.putExtra("postId",postId);
            startActivity(intent);
            finish();
        }
        if(v.getId() == R.id.text_comment_count_post){
            Log.d(TAG,"comment button clicked = "+"YES");
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra("from","notificationPostActivity");
            intent.putExtra("show","comments");
            intent.putExtra("count",post.getCommentCount());
            intent.putExtra("postId",postId);
            startActivity(intent);
            finish();
        }
    }

    private void getPost(int postId){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Result> call = service.getSinglePost(String.valueOf(postId));
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                if (response.code() == 200){
                    post = response.body();

                    userFullnameText.setText(post.getUser().getFullName());

                    String location = post.getUser().getCity()+", "+post.getUser().getCountry();
                    userLocationText.setText(location);

                    String profilePhotoUrl = post.getUser().getProfileImgThumbnail();
                    Glide.with(getApplicationContext()).load(profilePhotoUrl).centerCrop().into(userAvatarView);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
                    try {
                        long ts = Objects.requireNonNull(dateFormat.parse(new Utils().utcToLocalTime(post.getCreatedOn()))).getTime()/1000;
                        post.setCreatedOn(new Utils().getTimeAgo(ts));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    postTimeText.setText(post.getCreatedOn());
                    postText.setText(post.getText());


                    //Toggling like count text
                    if(post.getLikeCount() != 0){
                        likeCountText.setVisibility(View.VISIBLE);
                        if(post.getLikeCount() >1) {
                            String likeText = post.getLikeCount() + " likes";
                            likeCountText.setText(likeText);
                        }else{
                            String likeText = post.getLikeCount() + " like";
                            likeCountText.setText(likeText);
                        }
                    }else
                        likeCountText.setVisibility(View.GONE);

                    //Toggling comment count text
                    if (post.getCommentCount() != 0){
                        commentCountText.setVisibility(View.VISIBLE);
                        if(post.getCommentCount() >1) {
                            String commentText = post.getCommentCount() + " comments";
                            commentCountText.setText(commentText);
                        }else{
                            String commentText = post.getCommentCount() + " comment";
                            commentCountText.setText(commentText);
                        }
                    }else
                        commentCountText.setVisibility(View.GONE);

                    switch (post.getPosttype()){
                        case "I":
                            String imageUrl = post.getFiles().get(0).getMedia();
                            postImageView.setVisibility(View.VISIBLE);
                            Glide
                                    .with(getApplicationContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.landscape_image_placeholder)
                                    .into(postImageView);
                            break;
                        case "V":
                            if (post.getFiles().get(0).getAspect().equals("portrait")){
                                videoLandscapeLayout.setVisibility(View.GONE);
                                videoPortraitLayout.setVisibility(View.VISIBLE);
                                imageviewPortrait.setVisibility(View.VISIBLE);
                                if (post.getFiles().get(0).getThumbnail() != null) {
                                    Glide.with(getApplicationContext())
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
                                    Glide.with(getApplicationContext())
                                            .load(post.getFiles().get(0).getThumbnail())
                                            .into(imageviewLandscape);
                                }
                                videoPlayerLandscape.stopPlayer();
                                playButtonLandscape.setVisibility(View.VISIBLE);
                            }
                            break;
                        case "A":
                            videoPlayerLandscape.setVisibility(View.GONE);
                            videoPlayerPortrait.setVisibility(View.GONE);
                            audioPlayerLayout.setVisibility(View.VISIBLE);
                            audioPlayer.setVisibility(View.GONE);
                            playButtonAudio.setVisibility(View.VISIBLE);
                            break;
                    }

                    //Showing the data layout and hiding the shimmer layout
                    shimmerLinearLayout.setVisibility(View.GONE);
                    dataLinearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }
}