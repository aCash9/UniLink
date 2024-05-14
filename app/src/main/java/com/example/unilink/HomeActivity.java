package com.example.unilink;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
            } else if(id == R.id.marketplace) {
                openFragment(new MarketPlaceFragment(), false);
            } else if(id == R.id.menu) {
                openFragment(new AccountFragment(), false);
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