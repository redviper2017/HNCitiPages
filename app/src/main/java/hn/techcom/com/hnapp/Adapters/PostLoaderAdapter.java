package hn.techcom.com.hnapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Model.Post;
import hn.techcom.com.hnapp.R;

public class PostLoaderAdapter extends RecyclerView.Adapter<PostLoaderAdapter.ViewHolder> {

    private ArrayList<Post> postList;
    private static final String TAG = "PostLoaderAdapter";

    public PostLoaderAdapter(ArrayList<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_post,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView userImage;
        public MaterialTextView userName, userLocation, postTime, supportButton, postBody;
        public ViewPager imageSliderView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.userImage = itemView.findViewById(R.id.circleimageview_postedBy_image);
            this.userName = itemView.findViewById(R.id.textview_postedby_name);
            this.userLocation = itemView.findViewById(R.id.textview_postedfrom_location);
            this.postTime = itemView.findViewById(R.id.textview_postedat_time);
            this.supportButton = itemView.findViewById(R.id.text_support_post);
            this.postBody = itemView.findViewById(R.id.textview_post_body);
            imageSliderView = itemView.findViewById(R.id.image_slider_post);
        }
    }
}
