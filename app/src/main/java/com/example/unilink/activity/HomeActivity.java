package com.example.unilink.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.unilink.FirebaseController;
import com.example.unilink.Fragments.HomeFragment;
import com.example.unilink.Fragments.LivingFragment;
import com.example.unilink.Fragments.ReelsFragment;
import com.example.unilink.Fragments.SearchFragment;
import com.example.unilink.Fragments.SettingsFragment;
import com.example.unilink.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottom_navigation);


        //set on click listener to the bottom navigation view
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if(id == R.id.home) {
                count++;
                openFragment(new HomeFragment(), count == 1);
            } else if(id == R.id.reels) {
                openFragment(new ReelsFragment(), false);
            } else if(id == R.id.settings) {
                openFragment(new SettingsFragment(), false);
            } else if(id == R.id.search){
                openFragment(new SearchFragment(), false);
            } else {
                openFragment(new LivingFragment(), false);
            }

            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
    private void openFragment(Fragment fragment, boolean flag) {
        FragmentManager fm = getSupportFragmentManager();
        @SuppressLint("CommitTransaction") FragmentTransaction ft = fm.beginTransaction();
        if(flag)
            ft.add(R.id.container, fragment);
        else
            ft.replace(R.id.container, fragment);

        ft.commit();
    }
}