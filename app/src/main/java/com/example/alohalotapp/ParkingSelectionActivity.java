package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.alohalotapp.admin.FirebaseAdminHelperClass;
import com.example.alohalotapp.map.MapHelperClass;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;

public class ParkingSelectionActivity extends AppCompatActivity {
    private FirebaseAdminHelperClass firebaseHelper;
    private MapHelperClass mapHelper;

    private String userEmail;

    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_parking_selection);

        ImageView map = findViewById(R.id.map);
        firebaseHelper = new FirebaseAdminHelperClass();
        mapHelper = MapHelperClass.getInstance(map);

        database = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");

        Intent intentStart = getIntent();
        if (intentStart != null) {
            userEmail = intentStart.getStringExtra("userEmail");
        }

        map.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                map.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mapHelper.addMarkers(ParkingSelectionActivity.this, database);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapHelper.addMarkers(ParkingSelectionActivity.this, database);
    }
}