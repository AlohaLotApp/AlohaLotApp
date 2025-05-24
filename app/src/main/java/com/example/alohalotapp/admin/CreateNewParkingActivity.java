package com.example.alohalotapp.admin;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alohalotapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateNewParkingActivity extends AppCompatActivity {

    private EditText addParkingName, addLat, addLong, addCapacity;
    private Button addParking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_parking);

        addParkingName = findViewById(R.id.parkingName);
        addLat = findViewById(R.id.parkingLat);
        addLong = findViewById(R.id.parkingLong);
        addCapacity = findViewById(R.id.parkingCapacity);
        addParking = findViewById(R.id.addButton);

        addParking.setOnClickListener(view -> {
            String name = addParkingName.getText().toString().trim();
            String latStr = addLat.getText().toString().trim();
            String longStr = addLong.getText().toString().trim();
            String capStr = addCapacity.getText().toString().trim();

            if (name.isEmpty() || latStr.isEmpty() || longStr.isEmpty() || capStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int latitude = Integer.parseInt(latStr);
                int longitude = Integer.parseInt(longStr);
                int capacity = Integer.parseInt(capStr);

                addParkingSpaceToDb(name, latitude, longitude, capacity);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number input", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addParkingSpaceToDb(String name, int lat, int lon, int capacity) {
        Log.d(TAG, "Adding parking space...");

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference reference = rootNode.getReference("ParkingSpaces");

        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                long count = 0;
                if (task.getResult().exists()) {
                    count = task.getResult().getChildrenCount();
                }

                String newParkingId = "Parking" + (count + 1);
                ParkingSpace newSpace = new ParkingSpace(capacity, lat, lon, 0, name);

                reference.child(newParkingId).setValue(newSpace)
                        .addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                Toast.makeText(this, "Parking space added!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Failed to add parking space.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Firebase write failed", e);
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        });
            }
        });
    }
}