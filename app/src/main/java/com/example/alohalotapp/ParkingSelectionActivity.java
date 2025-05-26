package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alohalotapp.admin.FirebaseAdminHelperClass;
import com.example.alohalotapp.map.MapHelperClass;
import com.squareup.picasso.Picasso;

public class ParkingSelectionActivity extends AppCompatActivity {
    //temporary
    private FirebaseAdminHelperClass firebaseHelper;
    private MapHelperClass mapHelper;
    private static final String STATIC_MAP_API_KEY = "DUMMY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_selection);

        ImageView map = findViewById(R.id.map);
        firebaseHelper = new FirebaseAdminHelperClass();
        mapHelper = MapHelperClass.getInstance(map);

        map.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                map.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mapHelper.addMarkers(ParkingSelectionActivity.this);
            }
        });
    }

}