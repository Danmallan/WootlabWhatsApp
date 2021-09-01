package com.example.wootlabwhatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;

import com.example.wootlabwhatsapp.adapter.MainActivityAdapter;
import com.example.wootlabwhatsapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

//        Set up the viewpager with the adapter
        MainActivityAdapter adapter = new MainActivityAdapter(getSupportFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(adapter);

        setSupportActionBar(binding.toolBar);

//        binding tablayout
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.chats));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.status));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.calls));

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}