package com.example.alohalotapp.admin;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.function.Consumer;

public class FirebaseAdminHelperClass {

    private static final String TAG = "FirebaseAdminHelper";
    private static final String DATABASE_URL = "https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/";
    private final FirebaseDatabase firebaseDatabase;

    public FirebaseAdminHelperClass() {
        firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
    }

    public DatabaseReference getReference(String path) {
        return firebaseDatabase.getReference(path);
    }

    public DatabaseReference getParkingSpacesRef() {
        return getReference("parkingspaces");
    }

    public void addParkingSpace(String name, double lat, double lon, int capacity,String openTime,String closeTime,android.content.Context context) {
        DatabaseReference reference = getParkingSpacesRef();

        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long count = 0;
                if (task.getResult().exists()) {
                    count = task.getResult().getChildrenCount();
                }

                String newParkingId = "Parking" + (count + 1);
                ParkingSpace newSpace = new ParkingSpace(capacity, lat, lon, 0, name , openTime, closeTime);

                reference.child(newParkingId).setValue(newSpace)
                        .addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                Toast.makeText(context, "Parking space added!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to add parking space.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Firebase write failed", e);
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }

    public void loadParkingNames(Consumer<ArrayList<String>> onLoaded, Consumer<String> onError) {
        getParkingSpacesRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> names = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String name = child.child("Name").getValue(String.class);
                    if (name == null) {
                        name = child.child("name").getValue(String.class);
                    }
                    if (name != null) names.add(name);
                }
                onLoaded.accept(names);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onError.accept(error.getMessage());
            }
        });
    }

    public void loadCoordinates(Consumer<ArrayList<String>> onLoaded, Consumer<String> onError) {
        getParkingSpacesRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> coordinates = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Double latitude = child.child("Latitude").getValue(Double.class);
                    Double longitude = child.child("Longitude").getValue(Double.class);

                    if (latitude == null) {
                        latitude = child.child("latitude").getValue(Double.class);
                    }

                    if (longitude == null) {
                        longitude = child.child("longitude").getValue(Double.class);
                    }

                    StringBuilder coordinateBuilder = new StringBuilder();

                    if (latitude != null && longitude != null){
                        coordinateBuilder.append("&markers=color:red%7Clabel:P%7C");
                        coordinateBuilder.append(latitude);
                        coordinateBuilder.append(",");
                        coordinateBuilder.append(longitude);

                        coordinates.add(coordinateBuilder.toString());
                    }
                }
                onLoaded.accept(coordinates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                onError.accept(error.getMessage());
            }
        });
    }

    public void getParkingSpaceByName(String name, Consumer<ParkingSpaceWithId> onResult, Consumer<String> onError) {
        getParkingSpacesRef().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot child : task.getResult().getChildren()) {
                    String dbName = child.child("name").getValue(String.class);
                    if (name.equals(dbName)) {
                        ParkingSpace space = child.getValue(ParkingSpace.class);
                        if (space != null) {
                            onResult.accept(new ParkingSpaceWithId(child.getKey(), space));
                            return;
                        }
                    }
                }
                onError.accept("Parking not found");
            } else {
                onError.accept(task.getException().getMessage());
            }
        });
    }

    public void updateParkingSpace(String id, ParkingSpace updated, Runnable onSuccess, Consumer<String> onError) {
        getParkingSpacesRef().child(id).setValue(updated)
                .addOnSuccessListener(aVoid -> onSuccess.run())
                .addOnFailureListener(e -> onError.accept(e.getMessage()));
    }

    public static class ParkingSpaceWithId {
        public String id;
        public ParkingSpace space;

        public ParkingSpaceWithId(String id, ParkingSpace space) {
            this.id = id;
            this.space = space;
        }
    }
}
