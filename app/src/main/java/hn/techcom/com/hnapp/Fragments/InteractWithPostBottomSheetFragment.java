package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import hn.techcom.com.hnapp.R;

public class InteractWithPostBottomSheetFragment extends BottomSheetDialogFragment {

    private NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_interact_with_post_bottom_sheet, container, false);

        navigationView = view.findViewById(R.id.navigation_interact_with_post);
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.support_user_post){
                    Toast.makeText(getActivity(), "Support this user",Toast.LENGTH_LONG).show();
                }
                if(item.getItemId() == R.id.report_post){
                    Toast.makeText(getActivity(), "Reporting this post..",Toast.LENGTH_LONG).show();
                }
                if(item.getItemId() == R.id.delete_post){
                    Toast.makeText(getActivity(), "Deleting this post..",Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });
    }
}