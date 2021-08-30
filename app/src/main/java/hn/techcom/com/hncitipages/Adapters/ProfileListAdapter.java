package hn.techcom.com.hncitipages.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.R;

public class ProfileListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ProfileListAdapter";

    private Context context;
    private ArrayList<User> profilesList = new ArrayList<>();

    public ProfileListAdapter(Context context, ArrayList<User> profilesList) {
        this.context = context;
        this.profilesList = profilesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_likes, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User profile = profilesList.get(position);
        ((ProfileListAdapter.ProfileViewHolder) holder).bind(profile);
    }

    @Override
    public int getItemCount() {
        return profilesList == null ? 0 : profilesList.size();
    }

    private class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public MaterialTextView name, location, title;
        public CircleImageView avatar;

        public ProfileViewHolder(@NonNull View view) {
            super(view);

            name           = view.findViewById(R.id.name_post);
            title          = view.findViewById(R.id.title_post);
            location       = view.findViewById(R.id.location_post);
            avatar         = view.findViewById(R.id.avatar_post);
        }

        void bind(User profile) {
            String address = profile.getCity() + ", " + profile.getCountry();
            String user_title = profile.getTitle();

            //setting up user name and location
            name.setText(profile.getFullName());

            if (!user_title.equals("User")){
                title.setVisibility(View.VISIBLE);
                location.setVisibility(View.GONE);

                String user_title_text = user_title + ", HN CitiPages";
                title.setText(user_title_text);
            }else {
                if (profile.getCity().equals("N/A") || profile.getCountry().equals("N/A"))
                    location.setVisibility(View.GONE);
                else {
                    location.setVisibility(View.VISIBLE);
                    title.setVisibility(View.GONE);
                    location.setText(address);
                }
            }

            //setting up user avatar
            String profilePhotoUrl = profile.getProfileImgThumbnail();
//            Picasso
//                    .get()
//                    .load(profilePhotoUrl)
//                    .into(avatar);

            Glide.with(context).load(profilePhotoUrl).centerCrop().into(avatar);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
