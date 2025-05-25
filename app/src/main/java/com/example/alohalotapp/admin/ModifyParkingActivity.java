package com.example.alohalotapp.admin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alohalotapp.R;

public class ModifyParkingActivity extends AppCompatActivity {

    private EditText nameField, latField, longField, capacityField;
    private Button updateButton;
    private FirebaseAdminHelperClass firebaseHelper;
    private String parkingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_parking); // Reusing the layout

        nameField = findViewById(R.id.parkingName);
        latField = findViewById(R.id.parkingLat);
        longField = findViewById(R.id.parkingLong);
        capacityField = findViewById(R.id.parkingCapacity);
        updateButton = findViewById(R.id.addButton); // Same ID used, but we treat it as update

        updateButton.setText("Update Parking");

        firebaseHelper = new FirebaseAdminHelperClass();

        String selectedName = getIntent().getStringExtra("parking_name");

        firebaseHelper.getParkingSpaceByName(selectedName, result -> {
            parkingId = result.id;
            ParkingSpace space = result.space;

            nameField.setText(space.getName());
            latField.setText(String.valueOf(space.getLatitude()));
            longField.setText(String.valueOf(space.getLongitude()));
            capacityField.setText(String.valueOf(space.getCapacity()));

        }, error -> Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show());

        updateButton.setOnClickListener(v -> {
            try {
                String name = nameField.getText().toString().trim();
                double lat = Double.parseDouble(latField.getText().toString().trim());
                double lon = Double.parseDouble(longField.getText().toString().trim());
                int cap = Integer.parseInt(capacityField.getText().toString().trim());

                if (name.isEmpty()) {
                    Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParkingSpace updated = new ParkingSpace(cap, lat, lon, 0, name);

                firebaseHelper.updateParkingSpace(parkingId, updated,
                        () -> Toast.makeText(this, "Parking updated", Toast.LENGTH_SHORT).show(),
                        error -> Toast.makeText(this, "Update failed: " + error, Toast.LENGTH_SHORT).show()
                );

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
