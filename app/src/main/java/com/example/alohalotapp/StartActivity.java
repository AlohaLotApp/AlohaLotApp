package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StartActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private String userEmail ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

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
                return true;
            }
            else if (id == R.id.stats) {
                Intent intent = new Intent(StartActivity.this, StatisticsActivity.class);
                intent.putExtra("userEmail", userEmail); //sends the email to statistics
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
