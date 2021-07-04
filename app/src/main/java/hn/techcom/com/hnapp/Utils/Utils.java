package hn.techcom.com.hnapp.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Objects;

import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.LikeResponse;
import hn.techcom.com.hnapp.Models.Profile;
import hn.techcom.com.hnapp.Models.User;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {

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
}
