package com.kidd.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class GroupPageAdapter extends FragmentPagerAdapter{


    public GroupPageAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new Group();
                case 1:
                    return new ListGroup();
            }
            return null;
        }
        @Override
        public int getCount() {
            return 2;
        }
    }


