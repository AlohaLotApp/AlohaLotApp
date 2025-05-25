package com.example.alohalotapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class StatisticsActivity extends AppCompatActivity {

    private TextView usersEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_statistics);

        usersEmail = findViewById(R.id.statsImageTView);
        String email = getIntent().getStringExtra("userEmail");

        if (email != null) {
            usersEmail.setText(email);
        }


        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.stats);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(StatisticsActivity.this, StartActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.wallet) {
                startActivity(new Intent(StatisticsActivity.this, WalletActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.stats) {
                return true;
            }
            return false;
        });


    }
}