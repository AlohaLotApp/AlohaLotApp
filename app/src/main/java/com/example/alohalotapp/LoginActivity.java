package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.alohalotapp.admin.AdminMainActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    Button callSignUp;
    Button getInBtn;
    TextInputEditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        callSignUp = findViewById(R.id.signup_screen);
        getInBtn = findViewById(R.id.getInBtn);



        callSignUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        getInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = findViewById(R.id.email_input);
                String emailInput = email.getText().toString().trim();
                Intent intent;
                if((emailInput).equalsIgnoreCase("admin")){
                    intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                }
                else if((emailInput).equalsIgnoreCase("kikkat")){
                    intent = new Intent(LoginActivity.this, DurationActivity.class);
                }
                else {
                    intent = new Intent(LoginActivity.this, StartActivity.class);
                }
                startActivity(intent);

            }
        });

    }

}