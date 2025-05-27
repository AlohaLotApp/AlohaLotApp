package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StartActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        Button openMapbtn = findViewById(R.id.openMapBtn);

        openMapbtn.setOnClickListener( v->{
            Intent mapIntent  = new Intent(StartActivity.this, ParkingSelectionActivity.class);
            startActivity(mapIntent);
        });

        Intent intentSignUp = getIntent();
        if (intentSignUp != null) {
            userEmail = intentSignUp.getStringExtra("userEmail");
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                return true;
            } else if (id == R.id.wallet) {
                Intent intent = new Intent(StartActivity.this, WalletActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Remove animation
                return true;
            } else if (id == R.id.stats) {
                Intent intent = new Intent(StartActivity.this, StatisticsActivity.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
                overridePendingTransition(0, 0); // Remove animation
                return true;
            }
            return false;
        });
    }
}
