package hn.techcom.com.hncitipages.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.snov.timeagolibrary.PrettyTimeAgo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import hn.techcom.com.hncitipages.Fragments.CommentsFragment;
import hn.techcom.com.hncitipages.Fragments.LikesFragment;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.Models.Result;
import hn.techcom.com.hncitipages.Models.User;
import hn.techcom.com.hncitipages.R;

public class Utils {

    private static final String TAG = "Utils";

    //fetch registered user profile from local storage
    public Profile getNewUserFromSharedPreference(Context context) {
        SharedPreferences pref = Objects.requireNonNull(context).getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = pref.getString("NewUser","");
        Profile user = gson.fromJson(json, Profile.class);

        return user;
    }

    //store registered user to local storage
    public void storeNewUserToSharedPref(Context context, Profile newProfile) {
        SharedPreferences.Editor editor = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(newProfile);
        editor.putString("NewUser",json);
        editor.apply();
    }

    //converts name to camel case
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

    //store supporters & supporting profiles to shared preference
    public void storeProfiles(String profilesListType, ArrayList<User> profilesArrayList, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(profilesArrayList);
        editor.putString(profilesListType, json);
        editor.apply();
    }

    //remove media posts without file path
    public ArrayList<Result> removeMediaPostsWithoutFilePath(ArrayList<Result> postList){
        for(int i=0; i<postList.size(); i++)
            if (!postList.get(i).getPosttype().equals("S") && postList.get(i).getFiles().size() == 0)
                postList.remove(postList.get(i));

        return postList;
    }

    //set relative time in post list
    public ArrayList<Result> setPostRelativeTime(ArrayList<Result> postList){
        for (Result post : postList) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            try {
                long ts = Objects.requireNonNull(dateFormat.parse(utcToLocalTime(post.getCreatedOn()))).getTime()/1000;
                post.setCreatedOn(getTimeAgo(ts));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return postList;
    }

    //format UTC time to "Time Ago"
    public String getTimeAgo(long time){
        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;
        int WEEK_MILLIS = 7 * DAY_MILLIS;
        int MONTH_MILLIS = 4 * WEEK_MILLIS;
        int YEAR_MILLIS = 12 * MONTH_MILLIS;

        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = System.currentTimeMillis();
        Log.d(TAG,"post time now in milliseonds = "+now);
        Log.d(TAG,"system time now in milliseonds = "+time);
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 2 * DAY_MILLIS) {
            return "yesterday";
        } else if (diff < 7 * DAY_MILLIS){
            return diff / DAY_MILLIS + " days ago";
        } else if (diff < 2 * WEEK_MILLIS){
            return "a week ago";
        } else if (diff < 4L * WEEK_MILLIS){
            return diff / WEEK_MILLIS + " weeks ago";
        }else if (diff < 2L * MONTH_MILLIS){
            return "a month ago";
        }else if (diff < 12L * MONTH_MILLIS){
            return diff / MONTH_MILLIS + " months ago";
        }else if (diff < 2 * YEAR_MILLIS){
            return  "a year ago";
        }else
            return diff / YEAR_MILLIS + " years ago";
    }

    //convert UTC time to local time
    public String utcToLocalTime(String utcTime) {
        Log.d(TAG, "time got from server = " + utcTime);
        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        String dueDateAsNormal = "";
        try {
            value = oldFormatter.parse(utcTime);
            SimpleDateFormat newFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());

            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dueDateAsNormal;
    }

    public void onLikeCountClick(int postId, Context context){
        Fragment fragment = new LikesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("post_id",postId);
        fragment.setArguments(bundle);
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
    }

    public void onCommentCountClick(int postId, int count, Context context){
        Fragment fragment = new CommentsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("post_id",postId);
        bundle.putInt("count",count);
        fragment.setArguments(bundle);
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
    }
}
