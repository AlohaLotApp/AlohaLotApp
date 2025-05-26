package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alohalotapp.admin.FirebaseAdminHelperClass;
import com.squareup.picasso.Picasso;

public class ParkingSelectionActivity extends AppCompatActivity {
    //temporary
    private FirebaseAdminHelperClass firebaseHelper;
    private static final String STATIC_MAP_API_KEY = "DUMMY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_selection);

        ImageView map = findViewById(R.id.map);
        firebaseHelper = new FirebaseAdminHelperClass();

        map.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                map.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int width = map.getWidth();
                int height = map.getHeight();

                firebaseHelper.loadCoordinates(coordinatesList -> {
                    StringBuilder markerBuilder = new StringBuilder();
                    markerBuilder.append("&markers=color:red%7Clabel:P%7C");

                    for (int i = 0; i < coordinatesList.size(); i++) {
                        String marker = coordinatesList.get(i).replace("&markers=color:red%7Clabel:P%7C", "");
                        markerBuilder.append(marker);
                        if (i != coordinatesList.size() - 1) {
                            markerBuilder.append("%7C");
                        }
                    }

                    String mapUrl = "https://maps.googleapis.com/maps/api/staticmap"
                            + "?center=Honolulu,United+States"
                            + "&zoom=13"
                            + "&size=" + width + "x" + height
                            + markerBuilder
                            + "&key=" + STATIC_MAP_API_KEY;

                    Picasso.get().load(mapUrl).into(map);

                }, error -> {
                    Toast.makeText(ParkingSelectionActivity.this, "Failed to load coordinates: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

}