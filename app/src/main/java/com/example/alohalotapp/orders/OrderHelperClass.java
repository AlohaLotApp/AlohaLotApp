package com.example.alohalotapp.orders;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.alohalotapp.admin.FirebaseAdminHelperClass;
import com.example.alohalotapp.admin.ParkingSpace;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class OrderHelperClass {
    private static OrderHelperClass instance;

    private OrderHelperClass() {}

    public static OrderHelperClass getInstance() {
        if (instance == null)
            instance = new OrderHelperClass();

        return instance;
    }

    public void addOrder(Context context, FirebaseDatabase database, double amountPaid, String userId, String parkingName){
        FirebaseAdminHelperClass fireBaseHelper = new FirebaseAdminHelperClass();
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
                        .addOnSuccessListener(aVoid -> Toast.makeText(context, "Order added!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(context, "Failed to add order", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(context, "Failed to read orders", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
