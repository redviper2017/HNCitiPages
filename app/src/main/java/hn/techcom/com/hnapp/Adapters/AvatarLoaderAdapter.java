package hn.techcom.com.hnapp.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.R;

public class AvatarLoaderAdapter extends RecyclerView.Adapter<AvatarLoaderAdapter.ViewHolder> {

    private final ArrayList<String> avatarUrlList, nameList;
    private static final String TAG = "AvatarLoaderAdapter";

    public AvatarLoaderAdapter(ArrayList<String> avatarUrlList, ArrayList<String> nameList) {
        this.avatarUrlList = avatarUrlList;
        this.nameList = nameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_user_avatar, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "avatar url = " + avatarUrlList.get(position));
        Log.d(TAG, "name = " + nameList.get(position));

        String name = capitalizeName(nameList.get(position));
        holder.nameView.setText(name);

        if (avatarUrlList.get(position) != null) {
            String completeUrl = "http://hn.techcomengine.com" + avatarUrlList.get(position);
            Picasso
                    .get()
                    .load(completeUrl)
                    .into(holder.avatarView);
        }
    }

    @Override
    public int getItemCount() {
        return avatarUrlList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatarView;
        private MaterialTextView nameView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatarView = itemView.findViewById(R.id.circleimageview_row_avatar);
            this.nameView = itemView.findViewById(R.id.textview_row_avatar);
        }
    }

    public String capitalizeName(String name) {
        String fullName = "";
        String[] splited = name.split("\\s+");
        for (String part : splited) {
            if (fullName.equals(""))
                fullName = fullName + part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase();
            else
                fullName = fullName + " " + part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase();

        }
        return fullName;
    }
}
