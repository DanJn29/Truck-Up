package com.example.truckup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            // Start the main activity after the delay
            startActivity(new Intent(SplashActivity.this, Login.class));
            finish(); // Close the splash screen activity
        }, 3000); // 3 seconds delay, adjust as needed
    }
}