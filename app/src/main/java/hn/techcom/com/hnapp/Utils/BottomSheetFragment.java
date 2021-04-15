package hn.techcom.com.hnapp.Utils;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import hn.techcom.com.hnapp.R;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    NavigationView navigationView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottomsheet, container, false);
        navigationView = view.findViewById(R.id.navigation_view);
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
//                    case R.id.nav_visit_profile:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MyProfileFragment()).commit();
//                        dismiss();
//                        break;
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
//                    case R.id.nav_fav:
//                        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FavoritesFragment()).commit();
//                        dismiss();
//                        break;
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
//                    case R.id.nav_sign_out:
//                        MainActivity.session.logoutUser();
//                        break;
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
