package com.example.truckup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyViewPagerAdapter extends FragmentStateAdapter {

    public MyViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PostsFragment();
            case 1:
                return new Trucks_postsFragment();
            case 2:
                return new FavouritePostsFragment();
            default:
                return new PostsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // number of tabs
    }
}