package com.example.wootlabwhatsapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.wootlabwhatsapp.fragments.CallsFragment;
import com.example.wootlabwhatsapp.fragments.ChatsFragment;
import com.example.wootlabwhatsapp.fragments.StatusFragment;

public class MainActivityAdapter extends FragmentStateAdapter {

    public MainActivityAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new StatusFragment();
            case 2:
                return new CallsFragment();
        }

        return new ChatsFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
