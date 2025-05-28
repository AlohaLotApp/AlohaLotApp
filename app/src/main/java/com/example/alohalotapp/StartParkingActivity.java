package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class StartParkingActivity extends AppCompatActivity {
    TextInputLayout nameLayout, plateLayout, phoneLayout;
    TextInputEditText nameInput, plateInput, phoneInput;
    Button NextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start_parking);


        nameLayout = findViewById(R.id.name);
        plateLayout = findViewById(R.id.platenum);
        phoneLayout = findViewById(R.id.phonenum);

        nameInput = findViewById(R.id.name_input);
        plateInput = findViewById(R.id.platenum_input);
        phoneInput = findViewById(R.id.phonenum_input);

        NextBtn = findViewById(R.id.NextBtn);

        NextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString().trim();
                String plate = plateInput.getText().toString().trim();
                String phone = phoneInput.getText().toString().trim();

                boolean valid = true;


                if (name.isEmpty()) {
                    nameLayout.setError("Please enter your full name");
                    valid = false;
                } else {
                    nameLayout.setError(null);
                }

                if (plate.isEmpty()) {
                    plateLayout.setError("Please enter your license plate");
                    valid = false;
                } else {
                    plateLayout.setError(null);
                }

                if (phone.length() != 10 || !phone.matches("\\d+")) {
                    phoneLayout.setError("Phone number must be exactly 10 digits");
                    valid = false;
                } else {
                    phoneLayout.setError(null);
                }

                if (!valid) return;

            }
        });
    }
}
