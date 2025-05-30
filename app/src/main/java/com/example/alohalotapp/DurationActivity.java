package com.example.alohalotapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class DurationActivity extends AppCompatActivity {

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_duration);

        userEmail = getIntent().getStringExtra("userEmail");

        Button button1 = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);

        button1.setOnClickListener(v -> openPayment(3));
        button2.setOnClickListener(v -> openPayment(5));
        button3.setOnClickListener(v -> openPayment(11));
    }

    private void openPayment(int amount) {
        Intent intent = new Intent(DurationActivity.this, PaymentActivity.class);
        intent.putExtra("amountToPay", amount);
        intent.putExtra("userEmail", userEmail);
        startActivity(intent);
    }
}
