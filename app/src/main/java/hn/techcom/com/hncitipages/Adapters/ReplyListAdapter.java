package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Interfaces.OnReplyDeleteListener;
import hn.techcom.com.hncitipages.Interfaces.OnReplyOptionButtonClickListener;
import hn.techcom.com.hncitipages.Interfaces.ViewProfileListener;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.Reply;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class ReplyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    //Constants
    private static final String TAG = "ReplyListAdapter";
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<Reply> allReplies = new ArrayList<>();
    private ViewProfileListener viewProfileListener;
    private OnReplyOptionButtonClickListener onReplyOptionButtonClickListener;
    private int commentPosition = 0;

    public ReplyListAdapter(
            Context context,
            RecyclerView recyclerView,
            ArrayList<Reply> allReplies,
            ViewProfileListener viewProfileListener,
            OnReplyOptionButtonClickListener onReplyOptionButtonClickListener,
            int commentPosition) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.allReplies = allReplies;
        this.viewProfileListener = viewProfileListener;
        this.onReplyOptionButtonClickListener = onReplyOptionButtonClickListener;
        this.commentPosition = commentPosition;

        Log.d(TAG, "ReplyListAdapter called = "+"YES");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_replies, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Reply reply = allReplies.get(position);
        ((ReplyViewHolder) holder).bind(reply);
    }

    @Override
    public int getItemCount() {
        return allReplies == null ? 0 : allReplies.size();
    }

    private class ReplyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location;
        public CircleImageView avatar;
        private MaterialTextView replyPost;
        private ImageButton optionsButton;

        public ReplyViewHolder(@NonNull View view) {
            super(view);

            name           = view.findViewById(R.id.name_post);
            location       = view.findViewById(R.id.location_post);
            avatar         = view.findViewById(R.id.avatar_post);
            replyPost      = view.findViewById(R.id.reply_post);
            optionsButton  = view.findViewById(R.id.options_icon_reply);

            name.setOnClickListener(this);
            avatar.setOnClickListener(this);
            optionsButton.setOnClickListener(this);
        }

        void bind(Reply reply){
            String address = reply.getUser().getCity() + ", " + reply.getUser().getCountry();

            //setting up user name and location
            name.setText(reply.getUser().getFullName());
            location.setText(address);

            //setting up user avatar
            String profilePhotoUrl = reply.getUser().getProfileImgThumbnail();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(avatar);

            Glide.with(context).load(profilePhotoUrl).centerCrop().into(avatar);

            replyPost.setText(String.valueOf(reply.getComment()));

            Profile userProfile = new Utils().getNewUserFromSharedPreference(context);
            if (!userProfile.getHnid().equals(reply.getUser().getHnid()))
                optionsButton.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.name_post || view.getId() == R.id.avatar_post) {
                int position = getAbsoluteAdapterPosition();
                User user = allReplies.get(position).getUser();

                String hnid = user.getHnid();
                String name = user.getFullName();
                boolean isSupported = user.getIsSupported();

                viewProfileListener.viewProfile(hnid, name, isSupported);
            }
            if (view.getId() == R.id.options_icon_reply){
                int position = getAbsoluteAdapterPosition();
                User user = allReplies.get(position).getUser();
                String hnid = user.getHnid();

                onReplyOptionButtonClickListener.onReplyOptionButtonClick(commentPosition,hnid,allReplies.get(position).getId(),position);
            }
        }
    }
}
