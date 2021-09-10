package com.kidd.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class AdapterOtherProfile extends FragmentPagerAdapter {
    public AdapterOtherProfile(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new OtherPosts();
            case 1:
                return new OtherGroups();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
