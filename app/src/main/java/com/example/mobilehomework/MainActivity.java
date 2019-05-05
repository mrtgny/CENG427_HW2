package com.example.mobilehomework;

import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();

        if (actionBar != null){
            actionBar.hide();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new DailyFoodFragment(), "Daily Food");
        adapter.addFragment(new AnnouncementFragment(), "Announcemenet");
        adapter.addFragment(new NewsFragment(), "News");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        tabLayout.getTabAt(0).setIcon(R.drawable.baseline_local_dining_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.baseline_announcement_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.baseline_chrome_reader_mode_white);

    }
}