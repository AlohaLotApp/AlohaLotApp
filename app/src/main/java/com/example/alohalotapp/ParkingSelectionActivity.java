package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alohalotapp.admin.FirebaseAdminHelperClass;
import com.example.alohalotapp.map.MapHelperClass;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class ParkingSelectionActivity extends AppCompatActivity {
    private FirebaseAdminHelperClass firebaseHelper;
    private MapHelperClass mapHelper;
    private BottomNavigationView bottomNavigationView;

    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_parking_selection);

        ImageView map = findViewById(R.id.map);
        firebaseHelper = new FirebaseAdminHelperClass();
        mapHelper = MapHelperClass.getInstance(map);

        Intent intentStart = getIntent();
        if (intentStart != null) {
            userEmail = intentStart.getStringExtra("userEmail");
        }

        map.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                map.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mapHelper.addMarkers(ParkingSelectionActivity.this);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapHelper.addMarkers(ParkingSelectionActivity.this);
    }
}