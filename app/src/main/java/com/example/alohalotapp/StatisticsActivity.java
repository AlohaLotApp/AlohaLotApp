package com.example.alohalotapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class StatisticsActivity extends AppCompatActivity {

    private TextView usersEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        usersEmail = findViewById(R.id.statsImageTView);
        String email = getIntent().getStringExtra("userEmail");

        if (email != null) {
            usersEmail.setText(email);
        }



    }
}