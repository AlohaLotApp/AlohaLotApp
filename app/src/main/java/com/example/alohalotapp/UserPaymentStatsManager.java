package com.example.alohalotapp;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserPaymentStatsManager {

    private final Context context;
    private final String userId;
    private final DatabaseReference paymentStatsRef;

    public UserPaymentStatsManager(Context context) {
        this.context = context;
        SessionManager sessionManager = new SessionManager(context);
        this.userId = sessionManager.getUserId();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        this.paymentStatsRef = database.getReference("users").child(userId).child("paymentStats");
    }

    /**
     * Logs a payment of the given amount (e.g., 3, 5, 11).
     * Increments the count of that amount in the user's paymentStats node.
     */
    public void logPayment(int amount) {
        DatabaseReference amountRef = paymentStatsRef.child(String.valueOf(amount));

        amountRef.get().addOnSuccessListener(snapshot -> {
            Long currentCount = snapshot.getValue(Long.class);
            if (currentCount == null) currentCount = 0L;

            amountRef.setValue(currentCount + 1);
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Failed to log payment", Toast.LENGTH_SHORT).show();
        });
    }
}
