package hn.techcom.com.hnapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import hn.techcom.com.hnapp.Adapters.OnboardingFormLoaderAdapter;
import hn.techcom.com.hnapp.R;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class UserOnboardingFragment extends Fragment implements View.OnClickListener {

    private View nextButton, prevButton;
    private ViewPager viewPager;

    public UserOnboardingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_onboarding, container, false);

        //Hooks
        nextButton = view.findViewById(R.id.fab_next_onboarding_page_slider);
        prevButton = view.findViewById(R.id.fab_prev_onboarding_page_slider);

        viewPager = view.findViewById(R.id.page_slider_onboarding);
        viewPager.setAdapter(new OnboardingFormLoaderAdapter(getActivity().getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT));

        TabLayout tabLayout = view.findViewById(R.id.tab_indicator_page_slider_onboarding);
        tabLayout.setupWithViewPager(viewPager);

        //Click Listeners
        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switch (position) {
                    case 0:
                        prevButton.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        nextButton.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        prevButton.setVisibility(View.VISIBLE);
                        nextButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.fab_next_onboarding_page_slider) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        } else if (view.getId() == R.id.fab_prev_onboarding_page_slider) {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }
}