package com.example.alohalotapp.map;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.alohalotapp.ParkingSelectionActivity;
import com.example.alohalotapp.admin.FirebaseAdminHelperClass;
import com.squareup.picasso.Picasso;

public class MapHelperClass {
    private static MapHelperClass instance;
    private static final String STATIC_MAP_API_KEY = "AIzaSyC3MKrQF3FWy5IAyI1JXbgKaiMv-Thyd2o";
    private FirebaseAdminHelperClass firebaseHelper;
    private ImageView map;

    private MapHelperClass(ImageView map){
        this.map = map;
        this.firebaseHelper = new FirebaseAdminHelperClass();
    }

    public static MapHelperClass getInstance(ImageView map){
        if (instance == null)
            instance = new MapHelperClass(map);
        else
            //AYTO EDO EINAI TO ERROR
            map.instance = instance;
        return instance;
    }

    public void addMarkers(Context context){
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
            Toast.makeText(context, "Failed to load coordinates: " + error, Toast.LENGTH_LONG).show();
        });
    }
}
