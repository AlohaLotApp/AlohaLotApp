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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private TextView balanceText;
    private Button payButton;
    private int balance;
    private int amountToPay;
    private SessionManager sessionManager;
    private String userId;
    private String userEmail;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment);

        balanceText = findViewById(R.id.balance_text);
        payButton = findViewById(R.id.pay_button);

        // Διόρθωση: χρησιμοποίησε τα πεδία, όχι τοπικές μεταβλητές
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Πάρε σωστά τα extras
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        amountToPay = intent.getIntExtra("amountToPay", 5); // default value

        // Φόρτωσε το υπόλοιπο και ενημέρωσε το κουμπί
        loadBalance();
        payButton.setText("Pay $" + amountToPay);

        payButton.setOnClickListener(v -> {
            if (balance >= amountToPay) {
                balance -= amountToPay;
                saveBalance();
                balanceText.setText("Your balance: " + balance + " $");
                Toast.makeText(PaymentActivity.this, "Payment of $" + amountToPay + " successful!", Toast.LENGTH_SHORT).show();
                updateUserStatsInFirebase(amountToPay);
            } else {
                Toast.makeText(PaymentActivity.this, "Insufficient balance! Go to wallet!", Toast.LENGTH_SHORT).show();
            }

            // Πάντα επιστροφή στο StartActivity μετά από 3 δευτερόλεπτα
            new android.os.Handler().postDelayed(() -> {
                Intent i = new Intent(PaymentActivity.this, StartActivity.class);
                i.putExtra("userEmail", userEmail);
                startActivity(i);
                finish();
            }, 3000);
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

    private void updateUserStatsInFirebase(int amountPaid) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users").child(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Double currentAmountSpent = snapshot.child("amountSpent").getValue(Double.class);
                Long currentPointsLong = snapshot.child("points").getValue(Long.class);

                double currentAmount = currentAmountSpent != null ? currentAmountSpent : 0.0;
                int currentPoints = currentPointsLong != null ? currentPointsLong.intValue() : 0;

                double newAmountSpent = currentAmount + amountPaid;
                int newPoints = currentPoints + amountPaid;

                DatabaseReference paymentStatsRef = userRef.child("paymentStats");

                Long paid3 = snapshot.child("paymentStats").child("Paid3").getValue(Long.class);
                Long paid5 = snapshot.child("paymentStats").child("Paid5").getValue(Long.class);
                Long paid11 = snapshot.child("paymentStats").child("Paid11").getValue(Long.class);

                long newPaid3 = paid3 != null ? paid3 : 0;
                long newPaid5 = paid5 != null ? paid5 : 0;
                long newPaid11 = paid11 != null ? paid11 : 0;

                if (amountPaid == 3) newPaid3++;
                else if (amountPaid == 5) newPaid5++;
                else if (amountPaid == 11) newPaid11++;

                Map<String, Object> updates = new HashMap<>();
                updates.put("amountSpent", newAmountSpent);
                updates.put("points", newPoints);
                updates.put("paymentStats/Paid3", newPaid3);
                updates.put("paymentStats/Paid5", newPaid5);
                updates.put("paymentStats/Paid11", newPaid11);

                userRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "User stats updated!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to update stats", Toast.LENGTH_SHORT).show());

            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show());
    }
}
