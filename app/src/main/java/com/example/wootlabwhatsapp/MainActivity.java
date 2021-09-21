package com.example.wootlabwhatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wootlabwhatsapp.adapter.MainActivityAdapter;
import com.example.wootlabwhatsapp.databinding.ActivityMainBinding;
import com.example.wootlabwhatsapp.view.SettingsActivity;

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
                chageFabIcon(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.menu_search:
                Toast.makeText(this, "Action Search!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new_group:
                Toast.makeText(this, "Group!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_new_broadcast:
                Toast.makeText(this, "Broadcast!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_web:
                Toast.makeText(this, "Web!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_messages:
                Toast.makeText(this, "Messages!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void chageFabIcon(int index){
        binding.fabAction.hide();

        new Handler().postDelayed(() -> {
            switch (index){
                case 0:
                    binding.fabAction.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_message_24));
                    Toast.makeText(this, "Chats!", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    binding.fabAction.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_camera_alt_24));
                    Toast.makeText(this, "Status!", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    binding.fabAction.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_add_ic_call_24));
                    Toast.makeText(this, "Calls!", Toast.LENGTH_SHORT).show();
                    break;
            }
            binding.fabAction.show();
        }, 400);

    }
}