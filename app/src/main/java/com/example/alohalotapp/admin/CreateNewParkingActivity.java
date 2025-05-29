package com.example.alohalotapp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alohalotapp.R;


public class CreateNewParkingActivity extends AppCompatActivity {

    private EditText addParkingName, addLat, addLong, addCapacity,openTimeField,closeTimeField;

    private CheckBox handiCapped;
    private Button addParking;
    private FirebaseAdminHelperClass firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_new_parking);

        firebaseHelper = new FirebaseAdminHelperClass(); // <- initialize helper

        // Initialize the UI fields
        addParkingName = findViewById(R.id.parkingName);
        addLat = findViewById(R.id.parkingLat);
        addLong = findViewById(R.id.parkingLong);
        addCapacity = findViewById(R.id.parkingCapacity);
        addParking = findViewById(R.id.addButton);
        openTimeField = findViewById(R.id.openTime);
        closeTimeField = findViewById(R.id.closeTime);
        handiCapped = findViewById(R.id.handicapCheckbox);


        addParking.setOnClickListener(view -> {
            String name = addParkingName.getText().toString().trim();
            String latStr = addLat.getText().toString().trim();
            String longStr = addLong.getText().toString().trim();
            String capStr = addCapacity.getText().toString().trim();
            String openTime = openTimeField.getText().toString().trim();
            String closeTime = closeTimeField.getText().toString().trim();

            boolean ishandiCapped = handiCapped.isChecked();

            if (name.isEmpty() || latStr.isEmpty() || longStr.isEmpty() || capStr.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double latitude = Double.parseDouble(latStr);
                double longitude = Double.parseDouble(longStr);
                int capacity = Integer.parseInt(capStr);

                firebaseHelper.addParkingSpace(name, latitude, longitude, capacity,openTime,closeTime,ishandiCapped, this);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number input", Toast.LENGTH_SHORT).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close the activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
