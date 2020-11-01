package hn.techcom.com.hnapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.R;

public class AvatarLoaderAdapter extends RecyclerView.Adapter{

    private Context context;
    private  ArrayList avatarList;

    public AvatarLoaderAdapter(Context context, ArrayList avatarList) {
        this.context = context;
        this.avatarList = avatarList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user_avatar,parent,false);

        return new  AvatarHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

    public class AvatarHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView avatarView;

        public AvatarHolder(View view){
            super(view);
            avatarView = view.findViewById(R.id.circleimageview_row_avatar);
        }

        void bind(String avatar){
            Picasso
                    .get()
                    .load(avatar)
                    .resize(50,50)
                    .centerInside()
                    .into(avatarView);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
