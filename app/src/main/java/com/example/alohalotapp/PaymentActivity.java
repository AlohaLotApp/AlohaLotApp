package com.example.alohalotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.alohalotapp.admin.FirebaseAdminHelperClass;
import com.example.alohalotapp.admin.ParkingSpace;
import com.google.firebase.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    private TextView balanceText;
    private Button payButton;
    private int balance;
    private int amountToPay;
    private SessionManager sessionManager;
    private String userId;

    private FirebaseAdminHelperClass fireBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Μετάφερε την αρχικοποίηση εδώ για να έχει έγκυρο context
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        fireBaseHelper = new FirebaseAdminHelperClass();

        if (userId == null) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_payment);

        balanceText = findViewById(R.id.balance_text);
        payButton = findViewById(R.id.pay_button);

        Intent intent = getIntent();
        amountToPay = intent.getIntExtra("amountToPay", 5); // default
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");

        // Φόρτωσε το υπόλοιπο και ενημέρωσε UI
        loadBalance();
        payButton.setText("Pay $" + amountToPay);

        payButton.setOnClickListener(v -> {
            if (balance >= amountToPay) {
                balance -= amountToPay;
                saveBalance();
                balanceText.setText("Your balance: " + balance + " $");
                Toast.makeText(PaymentActivity.this, "Payment of $" + amountToPay + " successful!", Toast.LENGTH_SHORT).show();
                updateUserStatsInFirebase(amountToPay, database);
                addOrder(database, amountToPay);
            } else {
                Toast.makeText(PaymentActivity.this, "Insufficient balance! Go to wallet!", Toast.LENGTH_SHORT).show();
            }

            // Επιστροφή στο StartActivity μετά από 3 δευτερόλεπτα
            new android.os.Handler().postDelayed(() -> {
                Intent i = new Intent(PaymentActivity.this, StartActivity.class);
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

    private void updateUserStatsInFirebase(int amountPaid, FirebaseDatabase database) {
        DatabaseReference userRef = database.getReference("users").child(userId);

        userRef.get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Double currentAmountSpent = snapshot.child("amountSpent").getValue(Double.class);
                Long currentPointsLong = snapshot.child("points").getValue(Long.class);

                double currentAmount = currentAmountSpent != null ? currentAmountSpent : 0.0;
                int currentPoints = currentPointsLong != null ? currentPointsLong.intValue() : 0;

                double newAmountSpent = currentAmount + amountPaid;
                int newPoints = currentPoints + amountPaid;

                // Πάρε τις μετρήσεις μέσα στο paymentStats
                Long paid3 = snapshot.child("paymentStats").child("Paid3").getValue(Long.class);
                Long paid5 = snapshot.child("paymentStats").child("Paid5").getValue(Long.class);
                Long paid11 = snapshot.child("paymentStats").child("Paid11").getValue(Long.class);

                long newPaid3 = paid3 != null ? paid3 : 0;
                long newPaid5 = paid5 != null ? paid5 : 0;
                long newPaid11 = paid11 != null ? paid11 : 0;

                if (amountPaid == 3) newPaid3++;
                else if (amountPaid == 5) newPaid5++;
                else if (amountPaid == 11) newPaid11++;

                Map<String, Object> paymentStatsUpdates = new HashMap<>();
                paymentStatsUpdates.put("Paid3", newPaid3);
                paymentStatsUpdates.put("Paid5", newPaid5);
                paymentStatsUpdates.put("Paid11", newPaid11);

                Map<String, Object> updates = new HashMap<>();
                updates.put("amountSpent", newAmountSpent);
                updates.put("points", newPoints);
                updates.put("paymentStats", paymentStatsUpdates);

                userRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "User stats updated!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to update stats", Toast.LENGTH_SHORT).show());

            } else {
                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show());
    }

    private void addOrder(FirebaseDatabase database, double amountPaid){
        DatabaseReference orderRef = database.getReference("users").child(userId).child("orders");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        HashMap<Double, Integer> durationMap = new HashMap<>();
        durationMap.put(3.0, 30); //3$ 30 minutes
        durationMap.put(5.0, 60); //5$ 1 hours
        durationMap.put(11.0, 180); //11$ 3 hours


        orderRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long count = 0;
                if (task.getResult().exists()) {
                    count = task.getResult().getChildrenCount();
                }

                String newOrderKey = "order" + (count + 1);
                String parkingName = getIntent().getStringExtra("parkingName");

                Map<String, Object> order = new HashMap<>();
                order.put("arrivalTime", LocalTime.now().format(timeFormatter));
                order.put("departureTime", LocalTime.now().plusMinutes(durationMap.get(amountPaid)).format(timeFormatter));
                order.put("parkingName", parkingName);

                fireBaseHelper.getParkingSpaceByName(parkingName, parkingWithId -> {
                    ParkingSpace space = parkingWithId.space;
                    String id = parkingWithId.id;

                    int newCount = space.getCurrentUsers() + 1;
                    space.setCurrentUsers(newCount); // Update the value in the object

                    fireBaseHelper.updateParkingSpace(id, space,
                            () -> Log.d("Firebase", "User count decreased for " + parkingName),
                            error -> Log.e("Firebase", "Failed to update parking space: " + error)
                    );

                }, error -> Log.e("Firebase", "Could not find parking: " + error));

                // Save under /users/userId/orders/orderX
                orderRef.child(newOrderKey).setValue(order)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Order added!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed to add order", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Failed to read orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
