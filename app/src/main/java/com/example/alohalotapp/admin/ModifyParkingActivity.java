package com.example.alohalotapp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alohalotapp.R;

public class ModifyParkingActivity extends AppCompatActivity {

    private EditText nameField, latField, longField, capacityField, openField, closeField;
    private Button updateButton;
    private FirebaseAdminHelperClass firebaseHelper;
    private String parkingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_parking);

        nameField = findViewById(R.id.parkingName);
        latField = findViewById(R.id.parkingLat);
        longField = findViewById(R.id.parkingLong);
        capacityField = findViewById(R.id.parkingCapacity);
        openField = findViewById(R.id.openTime);
        closeField = findViewById(R.id.closeTime);
        updateButton = findViewById(R.id.addButton);

        updateButton.setText("Update Parking");

        firebaseHelper = new FirebaseAdminHelperClass();
        String selectedName = getIntent().getStringExtra("parking_name");

        // Fetch parking by name and populate fields
        firebaseHelper.getParkingSpaceByName(selectedName, result -> {
            parkingId = result.id;
            ParkingSpace space = result.space;

            nameField.setText(space.getName());
            latField.setText(String.valueOf(space.getLatitude()));
            longField.setText(String.valueOf(space.getLongitude()));
            capacityField.setText(String.valueOf(space.getCapacity()));
            openField.setText(space.getOpenTime());
            closeField.setText(space.getCloseTime());


            updateButton.setOnClickListener(v -> {
                String name = nameField.getText().toString().trim();
                String latStr = latField.getText().toString().trim();
                String lonStr = longField.getText().toString().trim();
                String capStr = capacityField.getText().toString().trim();
                String openTime = openField.getText().toString().trim();
                String closeTime = closeField.getText().toString().trim();

                if (name.isEmpty() || latStr.isEmpty() || lonStr.isEmpty() || capStr.isEmpty() ||
                        openTime.isEmpty() || closeTime.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    double lat = Double.parseDouble(latStr);
                    double lon = Double.parseDouble(lonStr);
                    int cap = Integer.parseInt(capStr);

                    ParkingSpace updated = new ParkingSpace(cap, lat, lon, space.getCurrentUsers(), name, openTime, closeTime);

                    firebaseHelper.updateParkingSpace(parkingId, updated,
                            () -> {
                                Toast.makeText(this, "Parking updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, AdminMainActivity.class));
                                finish();
                            },
                            error -> Toast.makeText(this, "Update failed: " + error, Toast.LENGTH_SHORT).show()
                    );

                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
                }
            });
        }, error -> Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show());
    }
}

