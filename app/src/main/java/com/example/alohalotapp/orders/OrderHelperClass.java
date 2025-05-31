package com.example.alohalotapp.orders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.alohalotapp.admin.FirebaseAdminHelperClass;
import com.example.alohalotapp.admin.ParkingSpace;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class OrderHelperClass {
    private static OrderHelperClass instance;
    private FirebaseAdminHelperClass fireBaseHelper;

    private OrderHelperClass() {
        fireBaseHelper = new FirebaseAdminHelperClass();
    }

    public static OrderHelperClass getInstance() {
        if (instance == null)
            instance = new OrderHelperClass();

        return instance;
    }

    public void addOrder(Context context, FirebaseDatabase database, double amountPaid, String userId, String parkingName){
        DatabaseReference orderRef = database.getReference("users").child(userId).child("orders");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yy");

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

                Map<String, Object> order = new HashMap<>();
                order.put("arrivalTime", LocalDateTime.now().format(timeFormatter));
                order.put("departureTime", LocalDateTime.now().plusMinutes(durationMap.get(amountPaid)).format(timeFormatter));
                order.put("parkingName", parkingName);

                Boolean hasExpired = false;
                order.put("hasExpired", hasExpired);

                fireBaseHelper.getParkingSpaceByName(parkingName, parkingWithId -> {
                    ParkingSpace space = parkingWithId.space;
                    String id = parkingWithId.id;

                    int newCount = space.getCurrentUsers() + 1;
                    space.setCurrentUsers(newCount);

                    fireBaseHelper.updateParkingSpace(id, space,
                            () -> Log.d("Firebase", "Current users increased for " + parkingName),
                            error -> Log.e("Firebase", "Failed to update parking space: " + error)
                    );

                }, error -> Log.e("Firebase", "Could not find parking: " + error));

                orderRef.child(newOrderKey).setValue(order)
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Order added!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to add order", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(context, "Failed to read orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkExpiredOrdersForAllUsers(Context context, FirebaseDatabase database) {
        DatabaseReference usersRef = database.getReference("users");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yy");
        LocalDateTime now = LocalDateTime.now();

        usersRef.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                for (DataSnapshot userSnapshot : userTask.getResult().getChildren()) {
                    String userId = userSnapshot.getKey();
                    Log.d("Firebase", "Checking user: " + userId);

                    DataSnapshot ordersSnapshot = userSnapshot.child("orders");
                    for (DataSnapshot orderSnapshot : ordersSnapshot.getChildren()) {
                        Map<String, Object> orderData = (Map<String, Object>) orderSnapshot.getValue();
                        if (orderData == null) continue;

                        String departureTimeStr = (String) orderData.get("departureTime");
                        Boolean hasExpired = (Boolean) orderData.get("hasExpired");
                        String parkingName = (String) orderData.get("parkingName");

                        if (departureTimeStr == null || hasExpired == null || hasExpired) continue;

                        try {
                            LocalDateTime departureTime = LocalDateTime.parse(departureTimeStr, formatter);
                            if (now.isAfter(departureTime)) {
                                // Expire the order
                                orderSnapshot.getRef().child("hasExpired").setValue(true);
                                Log.d("Firebase", "Marked order as expired: " + orderSnapshot.getKey());

                                // If a parkingName is attached, decrement usage safely
                                if (parkingName != null) {
                                    fireBaseHelper.getParkingSpaceByName(parkingName, parkingWithId -> {
                                        String parkingId = parkingWithId.id;

                                        DatabaseReference parkingRef = database.getReference("parkingspaces") // Adjust this if your path is different
                                                .child(parkingId)
                                                .child("currentUsers");

                                        parkingRef.runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                                Integer current = currentData.getValue(Integer.class);
                                                if (current == null) return Transaction.success(currentData);

                                                currentData.setValue(Math.max(0, current - 1));
                                                return Transaction.success(currentData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                                if (error != null) {
                                                    Log.e("Firebase", "Transaction failed: " + error.getMessage());
                                                } else {
                                                    Log.d("Firebase", "Parking usage decremented safely for " + parkingName);
                                                }
                                            }
                                        });
                                    }, error -> Log.e("Firebase", "Parking fetch error: " + error));
                                }
                            }
                        } catch (Exception e) {
                            Log.e("TimeParse", "Failed to parse: " + departureTimeStr, e);
                        }
                    }
                }
            } else {
                Log.e("Firebase", "User fetch failed", userTask.getException());
                Toast.makeText(context, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
