package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import hn.techcom.com.hnapp.R;

public class SharePostBottomSheetFragment extends BottomSheetDialogFragment {
    NavigationView navigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_share_post,container,false);
        navigationView = view.findViewById(R.id.navigation_view_share);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

//                switch (item.getItemId()) {
//
//                    case R.id.navigation_share_text:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ShareTextFragment()).commit();
//                        dismiss();
//                        break;
//                    case R.id.navigation_share_image:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ShareImageFragment()).commit();
//                        dismiss();
//                        break;
//                }

                return true;
            }
        });
    }
}
