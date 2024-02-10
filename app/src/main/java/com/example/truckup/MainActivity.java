package com.example.truckup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    MapFragment mapFragment = new MapFragment();
    TrucksFragment trucksFragment = new TrucksFragment();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                    return true;
                } else if (id == R.id.trucks) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, trucksFragment).commit();
                    return true;
                } else if (id == R.id.map) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, mapFragment).commit();
                    return true;
                } else if (id == R.id.profile) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                    return true;
                }
                return false;
            }
        });


    }
}