package com.example.alohalotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView balanceText;
    private Button payButton;
    private int balance;
    private int amountToPay;
    private SessionManager sessionManager;
    private String userId;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment);

        balanceText = findViewById(R.id.balance_text);
        payButton = findViewById(R.id.pay_button);

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userEmail = getIntent().getStringExtra("userEmail");
        amountToPay = getIntent().getIntExtra("amountToPay", 5); // default value

        loadBalance();

        payButton.setText("Pay $" + amountToPay);

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (balance >= amountToPay) {
                    balance -= amountToPay;
                    saveBalance();
                    balanceText.setText("Your balance: " + balance + " $");
                    Toast.makeText(PaymentActivity.this, "Payment of $" + amountToPay + " successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PaymentActivity.this, "Insufficient balance! Go to wallet!", Toast.LENGTH_SHORT).show();
                }

                new android.os.Handler().postDelayed(() -> {
                    Intent intent = new Intent(PaymentActivity.this, StartActivity.class);
                    intent.putExtra("userEmail", userEmail);
                    startActivity(intent);
                    finish();
                }, 3000);
            }
        });
    }

    private void loadBalance() {
        SharedPreferences prefs = getSharedPreferences("wallet_prefs", MODE_PRIVATE);
        balance = prefs.getInt("balance_" + userId, 0);
        balanceText.setText("Your balance: " + balance + " $");
    }

    private void saveBalance() {
        getSharedPreferences("wallet_prefs", MODE_PRIVATE)
                .edit()
                .putInt("balance_" + userId, balance)
                .apply();
    }
}
