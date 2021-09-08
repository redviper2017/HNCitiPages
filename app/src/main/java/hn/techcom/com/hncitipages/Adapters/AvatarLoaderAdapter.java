package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Interfaces.OnAvatarLongClickListener;
import hn.techcom.com.hncitipages.Interfaces.ViewProfileListener;
import hn.techcom.com.hncitipages.Models.SupportingProfileList;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.R;
import hn.techcom.com.hncitipages.Utils.Utils;

public class AvatarLoaderAdapter extends RecyclerView.Adapter<AvatarLoaderAdapter.ViewHolder> {

    private Context context;
    ArrayList<User> initialProfileList = new ArrayList<>();
    private ViewProfileListener viewProfileListener;
    private static final String TAG = "AvatarLoaderAdapter";
    private Utils myUtils;

    public AvatarLoaderAdapter(
            Context context,
            SupportingProfileList allProfiles,
            ViewProfileListener viewProfileListener) {
        this.context = context;
        this.viewProfileListener = viewProfileListener;

        initialProfileList.addAll(allProfiles.getResults());
        myUtils = new Utils();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_user_avatar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = initialProfileList.get(position);
        String name = myUtils.capitalizeName(user.getFullName());
        String avatarUrl = user.getProfileImgThumbnail();

        holder.nameView.setText(name);
        Glide.with(context).load(avatarUrl).centerCrop().into(holder.avatarView);
    }

    @Override
    public int getItemCount() {
        return initialProfileList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatarView;
        private MaterialTextView nameView;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatarView = itemView.findViewById(R.id.circleimageview_row_avatar);
            this.nameView   = itemView.findViewById(R.id.textview_row_avatar);
            this.layout     = itemView.findViewById(R.id.layout_row_user);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = initialProfileList.get(getAbsoluteAdapterPosition());
                    String hnid = user.getHnid();
                    String name = user.getFullName();

                    //As all profiles here are supporting profiles
                    viewProfileListener.viewProfile(hnid, name, true);
                }
            });
        }
    }
}
