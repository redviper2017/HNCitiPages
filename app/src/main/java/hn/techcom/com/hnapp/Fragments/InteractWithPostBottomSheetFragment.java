package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import hn.techcom.com.hnapp.Interfaces.GetDataService;
import hn.techcom.com.hnapp.Models.DeleteResponse;
import hn.techcom.com.hnapp.Network.RetrofitClientInstance;
import hn.techcom.com.hnapp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractWithPostBottomSheetFragment extends BottomSheetDialogFragment {

    private NavigationView navigationView;
    private int postId, itemPosition;

    private static final String TAG = "InteractWithPostBottomSheetFragment";

    public InteractWithPostBottomSheetFragment(int position, int id) {
        postId = id;
        itemPosition = position;
    }

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
                    deleteThisPost();
                }

                return true;
            }
        });
    }

    public void deleteThisPost(){
        Log.d(TAG,"delete post id = "+postId);
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<DeleteResponse> call = service.deletePost(postId);
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if(response.code() == 200){
                    DeleteResponse deleteResponse = response.body();
                    Toast.makeText(getActivity(), Objects.requireNonNull(deleteResponse).getSuccess(), Toast.LENGTH_LONG).show();
                    dismiss();
                }
                else
                    Toast.makeText(getActivity(),"Sorry, the post cannot be deleted at this moment. Try again..", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(getActivity(),"Sorry, the delete request has been failed. Try again..", Toast.LENGTH_LONG).show();
            }
        });
    }
}