package hn.techcom.com.hncitipages.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import hn.techcom.com.hncitipages.Activities.SignInActivity;
import hn.techcom.com.hncitipages.Fragments.FavoritesFragment;
import hn.techcom.com.hncitipages.Fragments.NotificationsFragment;
import hn.techcom.com.hncitipages.Fragments.ProfileSectionFragment;
import hn.techcom.com.hncitipages.Models.Profile;
import hn.techcom.com.hncitipages.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    NavigationView navigationView;
    private Utils myUtils;
    private Profile userProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottomsheet, container, false);
        navigationView = view.findViewById(R.id.navigation_view);

        myUtils = new Utils();
        userProfile = myUtils.getNewUserFromSharedPreference(getContext());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

//                    case R.id.nav_buy:
//                        startActivity(new Intent(getContext(),BuyBook.class));
//                        dismiss();
//                        break;
//                    case R.id.nav_notifications:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NotificationsFragment()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_today_post_hn:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TodayHNPostsFragment()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_all_post_hn:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AllHNPostsFragment()).commit();
//                        dismiss();
//                        break;
                    case R.id.nav_visit_notifications:
                        Fragment fragment= new NotificationsFragment();
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragment)).addToBackStack(null).commit();
                        dismiss();
                        break;
                    case R.id.nav_visit_profile:
                        if (userProfile.getPostCount()>0) {
                            Fragment fragmentSelected = new ProfileSectionFragment();

                            Bundle bundle = new Bundle();
                            bundle.putString("hnid", userProfile.getHnid());
                            bundle.putString("name", userProfile.getFullName());

                            fragmentSelected.setArguments(bundle);
                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main, Objects.requireNonNull(fragmentSelected)).addToBackStack(null).commit();
                        }else
                            Toast.makeText(getContext(),"You have to make your first post to view this section",Toast.LENGTH_SHORT).show();
                        dismiss();
                        break;
//                    case R.id.nav_random_community:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new RandomPostsByHNCommunityFragment()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_positive_thought_community:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PositiveThoughtPostsByHNCommunity()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_talent_community:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new TalentPostsByHNCommunity()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_culture_community:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new CulturePostsHNCommunity()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_news_community:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new NewsPostsByHNCommunity()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_emergency_community:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new EmergencyPostsHNCommunity()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_visit_city:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VisitCityFragment()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_visit_country:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VisitCountryFragment()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_visit_world:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new VisitWorldFragment()).commit();
//                        dismiss();
//                        break;
                    case R.id.nav_fav:
                        requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_main,new FavoritesFragment()).addToBackStack(null).commit();
                        dismiss();
                        break;
//                    case R.id.nav_about:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AboutUsFragment()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_contact:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ContactUsFragment()).commit();
//                        dismiss();
//                        break;
//                    case R.id.nav_settings:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingsFragment()).commit();
//                        dismiss();
//                        break;
                    case R.id.nav_sign_out:
//                        Toast.makeText(getContext(),"Signing out...", Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getActivity(), SignInActivity.class));
                        break;
//                    case R.id.nav_visit_supported_profile_posts:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SupportedProfileFragment()).commit();
//                        dismiss();
//                        break;
                }

                return true;
            }
        });
    }
}
