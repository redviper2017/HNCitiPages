package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Fragments.ProfileSectionFragment;
import hn.techcom.com.hncitipages.Interfaces.OnCommentOptionButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.OnLoadMoreListener;
import hn.techcom.com.hncitipages.Interfaces.OnReplyClickListener;
import hn.techcom.com.hncitipages.Interfaces.ViewProfileListener;
import hn.techcom.com.hncitipages.Models.Reply;
import hn.techcom.com.hncitipages.Models.ResultViewComments;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //Constants
    private static final String TAG = "CommentListAdapter";
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_COMMENT = 1;

    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<ResultViewComments> allComments = new ArrayList<>();
    private OnLoadMoreListener onLoadMoreListener;
    private OnReplyClickListener onReplyClickListener;
    private ViewProfileListener viewProfileListener;
    private OnCommentOptionButtonClickListener onCommentOptionButtonClickListener;

    private LinearLayoutManager linearLayoutManager;

    public CommentListAdapter(RecyclerView recyclerView, ArrayList<ResultViewComments> allComments, Context context, OnReplyClickListener onReplyClickListener, ViewProfileListener viewProfileListener, OnCommentOptionButtonClickListener onCommentOptionButtonClickListener) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.allComments = allComments;
        this.onReplyClickListener = onReplyClickListener;
        this.viewProfileListener = viewProfileListener;
        this.onCommentOptionButtonClickListener = onCommentOptionButtonClickListener;

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
        if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_loading_post, parent, false);
            return new LoadingViewHolder(view);
        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comments, parent, false);
            return new CommentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ResultViewComments comment = allComments.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_LOADING)
            ((LoadingViewHolder) holder).bind();
        else if (holder.getItemViewType() == VIEW_TYPE_COMMENT)
            ((CommentViewHolder) holder).bind(comment);

    }

    @Override
    public int getItemCount() {
        return allComments == null ? 0 : allComments.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (allComments.get(position) == null)
            return VIEW_TYPE_LOADING;
        else
            return VIEW_TYPE_COMMENT;
    }

    private class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ViewProfileListener{

        public MaterialTextView name, location, title;
        public CircleImageView avatar, replyAvatar;
        private MaterialTextView commentPost;
        private RecyclerView repliesRecyclerview;
        private ReplyListAdapter replyListAdapter;
        private ImageButton replyButton, postReplyButton, commentOptionsButton;
        private LinearLayout replyLayout;
        private EditText replyText;
        private Utils myUtils;
        public View supportCircle;

        public CommentViewHolder(@NonNull View view) {
            super(view);

            myUtils = new Utils();

            name                 = view.findViewById(R.id.name_post);
            title                = view.findViewById(R.id.title_post);
            location             = view.findViewById(R.id.location_post);
            avatar               = view.findViewById(R.id.avatar_post);
            replyAvatar          = view.findViewById(R.id.avatar_post_reply);
            commentPost          = view.findViewById(R.id.comment_post);
            repliesRecyclerview  = view.findViewById(R.id.recyclerview_posts_replies);
            replyButton          = view.findViewById(R.id.reply_button_comment);
            replyLayout          = view.findViewById(R.id.reply_layout);
            replyText            = view.findViewById(R.id.reply_editText);
            postReplyButton      = view.findViewById(R.id.post_reply_button);
            supportCircle        = view.findViewById(R.id.support_circle_view);
            commentOptionsButton = view.findViewById(R.id.options_icon_comment);

            replyButton.setOnClickListener(this);
            postReplyButton.setOnClickListener(this);
            name.setOnClickListener(this);
            avatar.setOnClickListener(this);
            commentOptionsButton.setOnClickListener(this);
        }

        void bind(ResultViewComments comment){
            String address = comment.getUser().getCity() + ", " + comment.getUser().getCountry();
            String user_title = comment.getUser().getTitle();
            boolean isSupported = comment.getUser().getIsSupported();

            //setting up user name and location
            name.setText(comment.getUser().getFullName());
            if (!user_title.equals("User")){
                title.setVisibility(View.VISIBLE);
                location.setVisibility(View.GONE);

                String user_title_text = user_title + ", HN CitiPages";
                title.setText(user_title_text);
            }else {
                if (comment.getUser().getCity().equals("N/A") || comment.getUser().getCountry().equals("N/A"))
                    location.setVisibility(View.GONE);
                else {
                    location.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    location.setText(address);
                }
            }

            //setting up user avatar
            String profilePhotoUrl = comment.getUser().getProfileImgThumbnail();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(avatar);

            if (isSupported)
                supportCircle.setVisibility(View.VISIBLE);
            else
                supportCircle.setVisibility(View.GONE);

            Glide.with(context).load(profilePhotoUrl).centerCrop().into(avatar);

            Glide.with(context).load(myUtils.getNewUserFromSharedPreference(context).getProfileImgThumbnail()).centerCrop().into(replyAvatar);

            Log.d(TAG,"comment = "+comment.getComment());
            commentPost.setText(String.valueOf(comment.getComment()));

            if(comment.getReplies().size() > 0) {
                ArrayList<Reply> replyList = new ArrayList<>(comment.getReplies());
                setRecyclerView(replyList, repliesRecyclerview);
            }
        }

        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.reply_button_comment) {
                if(replyButton.getTag().equals("reply")) {
                    replyButton.setTag("cancel");
                    int position = getAbsoluteAdapterPosition();

                    replyLayout.setVisibility(View.VISIBLE);
                    replyButton.setImageResource(R.drawable.cancel_ic);
                }else{
                    replyButton.setTag("reply");
                    replyButton.setImageResource(R.drawable.reply_ic);
                    replyLayout.setVisibility(View.GONE);
                }
            }
            if(view.getId() == R.id.post_reply_button){
                int position = getAbsoluteAdapterPosition();
                int commentId = allComments.get(position).getId();
                if(!TextUtils.isEmpty(replyText.getText().toString()))
                    onReplyClickListener.onReplyClick(commentId,replyText.getText().toString(), position, replyLayout, replyButton);
                else
                    Toast.makeText(context,"Oops! You've forgot to enter your reply",Toast.LENGTH_LONG).show();
            }
            if (view.getId() == R.id.name_post || view.getId() == R.id.avatar_post) {
                int position = getAbsoluteAdapterPosition();
                User user = allComments.get(position).getUser();

                String hnid = user.getHnid();
                String name = user.getFullName();
                boolean isSupported = user.getIsSupported();

                viewProfileListener.viewProfile(hnid, name, isSupported);
            }
            if (view.getId() == R.id.options_icon_comment)
                onCommentOptionButtonClickListener.onCommentOptionButtonClick(allComments.get(getAbsoluteAdapterPosition()).getId(),allComments.get(getAbsoluteAdapterPosition()).getUser().getHnid(),getAbsoluteAdapterPosition());
        }

        public void setRecyclerView(ArrayList<Reply> replyList, RecyclerView repliesRecyclerview){
            linearLayoutManager = new LinearLayoutManager(context);
            repliesRecyclerview.setLayoutManager(linearLayoutManager);
            replyListAdapter = new ReplyListAdapter(context, this.repliesRecyclerview, replyList, this);
            repliesRecyclerview.setAdapter(replyListAdapter);
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
