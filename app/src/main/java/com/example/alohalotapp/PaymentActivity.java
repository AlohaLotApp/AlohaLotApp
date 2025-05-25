package com.example.alohalotapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView balanceText;
    private Button payButton;
    private int balance = 10;
    private int amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        balanceText = findViewById(R.id.balance_text);
        payButton = findViewById(R.id.pay_button);

        amount = getIntent().getIntExtra("amount", 0);

        balanceText.setText("Your balance: " + balance + " €");
        payButton.setText("Pay " + amount + " €");

        payButton.setOnClickListener(v -> {
            if (balance >= amount) {
                balance -= amount;
                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                balanceText.setText("Your balance: " + balance + " €");
                payButton.setEnabled(false);
            } else {
                Toast.makeText(this, "Insufficient balance", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
