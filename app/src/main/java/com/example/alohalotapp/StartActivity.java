package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StartActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                return true;
            } else if (id == R.id.wallet) {
                Intent intent = new Intent(StartActivity.this, WalletActivity.class);
                startActivity(intent);
                return true;
            }
            else if (id == R.id.stats) {
                Intent intent = new Intent(StartActivity.this, StatisticsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
