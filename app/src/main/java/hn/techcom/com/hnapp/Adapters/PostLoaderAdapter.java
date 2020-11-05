package hn.techcom.com.hnapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import hn.techcom.com.hnapp.Models.Post;
import hn.techcom.com.hnapp.R;

public class PostLoaderAdapter extends RecyclerView.Adapter<PostLoaderAdapter.ViewHolder> {

    private ArrayList<Post> postList;
    private Context context;
    private static final String TAG = "PostLoaderAdapter";

    public PostLoaderAdapter(ArrayList<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.row_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String fullname = postList.get(position).getUser().getFirstName() + " " + postList.get(position).getUser().getLastName();
        String location = postList.get(position).getUser().getCity() + ", " + postList.get(position).getUser().getCountry();

        Picasso.get()
                .load("http://hn.techcomengine.com" + postList.get(position).getUser().getProfileImgUrl())
                .fit()
                .centerInside()
                .into(holder.userImage);
        holder.userName.setText(fullname);
        holder.userLocation.setText(location);
        holder.postTime.setText(postList.get(position).getCreatedOn());
        holder.postBody.setText(postList.get(position).getText());

        if (postList.get(position).getType().equals("I")) {
            holder.imageSliderView.setVisibility(View.VISIBLE);


            for (Post post : postList) {
                ArrayList<String> imageList = new ArrayList<>();
                if (post.getType().equals("I")) {
                    imageList.add(post.getImageUrl());
                    ImageLoaderAdapter adapter = new ImageLoaderAdapter(context, imageList);
                    holder.imageSliderView.setAdapter(adapter);
                }
            }

            if (postList.get(position).getType().equals("I")){
                ArrayList<String> imageList = new ArrayList<>();
                imageList.add(postList.get(position).getImageUrl());
                ImageLoaderAdapter adapter = new ImageLoaderAdapter(context, imageList);
                holder.imageSliderView.setAdapter(adapter);
            }


        } else
            holder.imageSliderView.setVisibility(View.GONE);

        if (postList.get(position).getSupport().equals(true))
            holder.supportButton.setText(context.getResources().getString(R.string.supporting));
        else
            holder.supportButton.setText(context.getResources().getString(R.string.support));

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public ArrayList<String> getPostedImageUrls(ArrayList<Post> postList) {
        ArrayList<String> imageList = new ArrayList<>();
        for (Post post : postList)
            imageList.add(post.getImageUrl());
        return imageList;
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
