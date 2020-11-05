package hn.techcom.com.hnapp.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import hn.techcom.com.hnapp.Models.Post;

public class ImageLoaderAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> imageList;

    public ImageLoaderAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);

        Log.d("ImageLoaderAdapter","view pager image url = "+imageList.get(position));

        Picasso.get()
                .load("http://hn.techcomengine.com"+imageList.get(position))
                .fit()
                .centerInside()
                .into(imageView);

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
