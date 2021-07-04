package hn.techcom.com.hnapp.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Interfaces.OnAvatarLongClickListener;
import hn.techcom.com.hnapp.R;

public class AvatarLoaderAdapter extends RecyclerView.Adapter<AvatarLoaderAdapter.ViewHolder> {

    private final ArrayList<String> avatarUrlList, nameList, hnidList;
    private static final String TAG = "AvatarLoaderAdapter";

    private final OnAvatarLongClickListener onAvatarLongClickListener;

    public AvatarLoaderAdapter(ArrayList<String> avatarUrlList, ArrayList<String> nameList, ArrayList<String> hnidList, OnAvatarLongClickListener onAvatarLongClickListener) {
        this.avatarUrlList = avatarUrlList;
        this.nameList = nameList;
        this.hnidList = hnidList;
        this.onAvatarLongClickListener = onAvatarLongClickListener;
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
            String completeUrl = avatarUrlList.get(position);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView avatarView;
        private MaterialTextView nameView;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatarView = itemView.findViewById(R.id.circleimageview_row_avatar);
            this.nameView   = itemView.findViewById(R.id.textview_row_avatar);
            this.layout     = itemView.findViewById(R.id.layout_row_user);

            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.d(TAG,"View this profile of = "+nameList.get(getAbsoluteAdapterPosition()) + " " + hnidList.get(getAbsoluteAdapterPosition()));
                    onAvatarLongClickListener.onAvatarLongClick(hnidList.get(getAbsoluteAdapterPosition()), nameList.get(getAbsoluteAdapterPosition()));
                    return true;
                }
            });
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
