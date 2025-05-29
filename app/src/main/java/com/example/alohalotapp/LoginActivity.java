package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.alohalotapp.admin.AdminMainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    Button callSignUp;
    Button getInBtn;
    TextInputEditText email, password;
    FirebaseDatabase reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        callSignUp = findViewById(R.id.signup_screen);
        getInBtn = findViewById(R.id.getInBtn);
        email = findViewById(R.id.email_input);
        password = findViewById(R.id.password_input);

        reference = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        getInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailInput = email.getText().toString().trim();
                String passwordInput = password.getText().toString().trim();

                if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fill in email and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                reference.getReference("users").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean found = false;

                        for (DataSnapshot userSnapshot : task.getResult().getChildren()) {
                            String dbEmail = userSnapshot.child("regEmail").getValue(String.class);
                            String dbPassword = userSnapshot.child("regPassword").getValue(String.class);

                            if (emailInput.equalsIgnoreCase(dbEmail)) {
                                found = true;

                                if (passwordInput.equalsIgnoreCase(dbPassword)) {
                                    String userId = userSnapshot.getKey(); // This is the Firebase UID
                                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                    sessionManager.saveUserId(userId);

                                    if (emailInput.equalsIgnoreCase("admin")) {
                                        startActivity(new Intent(LoginActivity.this, AdminMainActivity.class));
                                    }else {
                                        startActivity(new Intent(LoginActivity.this, StartActivity.class));
                                    }
                                    finish(); // Prevent going back to login screen
                                } else {
                                    Toast.makeText(LoginActivity.this, "Wrong Password!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }
                        }

                        if (!found) {
                            Toast.makeText(LoginActivity.this, "No user with this email", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Database connection failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
