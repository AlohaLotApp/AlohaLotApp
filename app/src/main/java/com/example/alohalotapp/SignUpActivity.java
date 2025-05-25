package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SignUpActivity extends AppCompatActivity {

    TextInputLayout regEmail,regPassword,regConfirm;
    Button regBtn;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        regEmail = findViewById(R.id.registerEmail);
        regPassword = findViewById(R.id.registerPassword);
        regConfirm = findViewById(R.id.registerConfPassword);
        regBtn = findViewById(R.id.SignUpBtn);

        //Save data in Firebase on buttonClick
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //includes all the elements of the database
                rootNode = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");
                reference = rootNode.getReference("users");

                //Get all the values
                String email = regEmail.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                String confPassword = regConfirm.getEditText().getText().toString();

                if (!password.equals(confPassword)) {
                    regConfirm.setError("Passwords do not match");
                    return;
                } else {
                    regConfirm.setError(null);
                }

                UserHelperClass helperClass = new UserHelperClass(email, password, confPassword);

                // Count how many users already exist to create a new incremental ID (User1, User2, ...)
                reference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long count = 0;
                        if (task.getResult().exists()) {
                            count = task.getResult().getChildrenCount(); // e.g. 2 users already exist
                        }

                        // Create the new ID (User3, if there are already 2 users)
                        String userId = "User" + (count + 1);

                        // Save the new user under that ID
                        reference.child(userId).setValue(helperClass).addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                Intent intent = new Intent(SignUpActivity.this, StartActivity.class);
                                intent.putExtra("userEmail", email); //sends the email to statistics (signup -> start -> stats)
                                startActivity(intent);
                                finish();

                            }
                        });
                    }
                });


            }
        });
    }
}