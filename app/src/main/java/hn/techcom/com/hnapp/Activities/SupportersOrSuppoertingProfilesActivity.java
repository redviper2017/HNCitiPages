package hn.techcom.com.hnapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Locale;

import hn.techcom.com.hnapp.Adapters.LikeListAdapter;
import hn.techcom.com.hnapp.Adapters.ProfileListAdapter;
import hn.techcom.com.hnapp.Models.Result;
import hn.techcom.com.hnapp.Models.ResultViewLikes;
import hn.techcom.com.hnapp.Models.User;
import hn.techcom.com.hnapp.R;

public class SupportersOrSuppoertingProfilesActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "SSProfilesActivity";
    private String showListOf;
    private RecyclerView recyclerView;
    private ProfileListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supporters_or_suppoerting_profiles);

        showListOf = getIntent().getStringExtra("Show");

        //Hooks
        ImageButton backButton       = findViewById(R.id.image_button_back);
        MaterialTextView screenTitle = findViewById(R.id.text_screen_title);
        MaterialTextView countText   = findViewById(R.id.count_text);
        recyclerView                 = findViewById(R.id.recyclerview);

        ArrayList<User> profilesList = getProfiles();

        if (showListOf.equals("Supporters")) {
            String count = getIntent().getStringExtra("SupporterCount");
            countText.setText(count);
            if (Integer.parseInt(count) > 1)
                screenTitle.setText(showListOf);
            else
                screenTitle.setText("Supporter");
        }
        else {
            String count = getIntent().getStringExtra("SupportingCount");
            countText.setText(count);
            if (Integer.parseInt(count) > 1)
                screenTitle.setText(showListOf);
            else
                screenTitle.setText("Supporting Profile");
        }
        setRecyclerView(profilesList);

        Log.d(TAG,"number of "+showListOf+" = "+profilesList.size());

        //OnClick Listeners
        backButton.setOnClickListener(this);
    }

    private ArrayList<User> getProfiles(){
        SharedPreferences sharedPreferences = this.getSharedPreferences("MY_PREF", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json;
        if (showListOf.equals("Supporters"))
            json = sharedPreferences.getString("Supporters", null);
        else
            json = sharedPreferences.getString("Supporting", null);

        Type type = new TypeToken<ArrayList<User>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.image_button_back)
            super.onBackPressed();
    }

    public void setRecyclerView(ArrayList<User> profilesList){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProfileListAdapter(this, profilesList);
        recyclerView.setAdapter(adapter);
    }
}