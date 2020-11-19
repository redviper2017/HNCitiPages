package hn.techcom.com.hnapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import hn.techcom.com.hnapp.Fragments.OnboardingUserAboutFragment;
import hn.techcom.com.hnapp.Fragments.OnboardingUserContactFragment;
import hn.techcom.com.hnapp.Fragments.OnboardingUserLocationFragment;
import hn.techcom.com.hnapp.Fragments.OnboardingUserProfileDataFragment;

public class OnboardingFormLoaderAdapter extends FragmentPagerAdapter {

    public OnboardingFormLoaderAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OnboardingUserContactFragment();
            case 1:
                return new OnboardingUserAboutFragment();
            case 2:
                return new OnboardingUserLocationFragment();
            case 3:
                return new OnboardingUserProfileDataFragment();
        }
        return new Fragment();
    }

    @Override
    public int getCount() {
        return 4;
    }
}
