package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    // UI fields for email, password, confirm password
    TextInputLayout regEmail, regPassword, regConfirm;
    // Sign up button
    Button regBtn;

    // Firebase database references
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    // Holds the generated user ID
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fullscreen mode (hides status bar)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        // Link UI fields
        regEmail = findViewById(R.id.registerEmail);
        regPassword = findViewById(R.id.registerPassword);
        regConfirm = findViewById(R.id.registerConfPassword);
        regBtn = findViewById(R.id.SignUpBtn);

        // On button click
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize Firebase
                rootNode = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");
                reference = rootNode.getReference("users");

                // Get input values
                String email = regEmail.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                String confPassword = regConfirm.getEditText().getText().toString();

                // Validate fields
                if (email.isEmpty()) {
                    regEmail.setError("Email is required");
                    return;
                } else {
                    regEmail.setError(null);
                }

                if (password.isEmpty()) {
                    regPassword.setError("Password is required");
                    return;
                } else {
                    regPassword.setError(null);
                }

                if (confPassword.isEmpty()) {
                    regConfirm.setError("Please confirm your password");
                    return;
                } else {
                    regConfirm.setError(null);
                }

                // Check if passwords match
                if (!password.equals(confPassword)) {
                    regConfirm.setError("Passwords do not match");
                    return;
                } else {
                    regConfirm.setError(null);
                }

                // Create user object
                UserHelperClass helperClass = new UserHelperClass(email, password, 0, 0.0, 0);

                // Count existing users
                reference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long count = 0;
                        if (task.getResult().exists()) {
                            count = task.getResult().getChildrenCount();
                        }

                        // Create unique user ID
                        userId = "User" + (count + 1);

                        // Save new user
                        reference.child(userId).setValue(helperClass).addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                // Save ID in session
                                SessionManager sessionManager = new SessionManager(SignUpActivity.this);
                                sessionManager.saveUserId(userId);

                                // Set default usage stats
                                initializeUserUsageStats(userId);

                                // Go to next screen
                                Intent intent = new Intent(SignUpActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private void initializeUserUsageStats(String userId) {
        DatabaseReference parkingsRef = rootNode.getReference("parkingspaces");
        DatabaseReference userUsageStatsRef = rootNode.getReference("users").child(userId).child("usageStats");

        // Get all parking data
        parkingsRef.get().addOnCompleteListener(parkingTask -> {
            if (parkingTask.isSuccessful() && parkingTask.getResult().exists()) {
                DataSnapshot parkingSnapshot = parkingTask.getResult();

                Map<String, Integer> usageStats = new HashMap<>();

                for (DataSnapshot parkingEntry : parkingSnapshot.getChildren()) {
                    String parkingName = parkingEntry.child("name").getValue(String.class);

                    if (parkingName != null) {
                        usageStats.put(parkingName, 0);
                    }
                }

                // Save usage stats with parking names as keys
                userUsageStatsRef.setValue(usageStats);
            }
        });
    }

    // Return userId if needed
    public String getUserId() {
        return userId;
    }
}
