package com.example.alohalotapp.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.alohalotapp.LoginActivity;
import com.example.alohalotapp.SignUpActivity;
import com.example.alohalotapp.StartParkingActivity;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MapHelperClass {
    private static MapHelperClass instance;
    private static final String STATIC_MAP_API_KEY = "AIzaSyDN1edvlQjSdtFq8oH3jT2msvcrbg6_EYU";
    private ImageView map;

    private MapHelperClass(ImageView map) {
        this.map = map;
    }

    public static MapHelperClass getInstance(ImageView map) {
        if (instance == null)
            instance = new MapHelperClass(map);
        else
            instance.setMap(map);
        return instance;
    }

    public void setMap(ImageView map) {
        this.map = map;
    }

    public void addMarkers(Context context) {
        if (map == null || map.getWidth() == 0 || map.getHeight() == 0) {
            System.out.println("Map not ready yet!");
            return;
        }

        int width = map.getWidth();
        int height = map.getHeight();

        ParkingData parkingData = new ParkingData();
        ArrayList<String> openParkingSpotsList = new ArrayList<>();

        parkingData.getCoordinates(coordinatesList -> {
            if (coordinatesList == null || coordinatesList.isEmpty()) {
                Toast.makeText(context, "No parking spots to show on map.", Toast.LENGTH_LONG).show();
                return;
            }


            parkingData.getCapacities(capacitiesList -> {
                parkingData.getCurrentUsers(currentUsersList -> {
                    parkingData.getIsHandicapped(isHandicappedList -> {
                        parkingData.getOpeningHours(openingHoursList -> {


                            StringBuilder markerBuilder = new StringBuilder();
                            int size = Math.min(coordinatesList.size(),
                                    Math.min(capacitiesList.size(),
                                            Math.min(currentUsersList.size(), isHandicappedList.size())));

                            LocalTime now = LocalTime.now();

                            for (int i = 0; i < size; i++) {
                                String color = "red"; //default

                                try {
                                    LocalTime openingTime = LocalTime.parse(openingHoursList.get(i).first);
                                    LocalTime closingTime = LocalTime.parse(openingHoursList.get(i).second);

                                    boolean isOpen = !now.isBefore(openingTime) && now.isBefore(closingTime);

                                    if (!isOpen || capacitiesList.get(i).equals(currentUsersList.get(i))) {
                                        color = "black"; //if closed
                                    } else if (Boolean.TRUE.equals(isHandicappedList.get(i))) {
                                        color = "blue"; //if not closed and for handicapped people
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (color != "black")
                                    openParkingSpotsList.add(coordinatesList.get(i));


                                markerBuilder.append("&markers=color:")
                                        .append(color)
                                        .append("%7Clabel:P%7C")
                                        .append(coordinatesList.get(i));
                            }

                            String mapUrl = "https://maps.googleapis.com/maps/api/staticmap"
                                    + "?center=Honolulu,United+States"
                                    + "&zoom=13"
                                    + "&size=" + width + "x" + height
                                    + markerBuilder
                                    + "&key=" + STATIC_MAP_API_KEY;


                            Picasso.get().load(mapUrl).into(map, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    addButtons(context, openParkingSpotsList);
                                }

                                @Override
                                public void onError(Exception e) {
                                    Toast.makeText(context, "Failed to load map image", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }, error -> showError(context, "opening hours"));
                    }, error -> showError(context, "handicapped info"));
                }, error -> showError(context, "current users"));
            }, error -> showError(context, "capacities"));

        }, error -> showError(context, "coordinates"));
    }
    private void showError(Context context, String dataName) {
        Toast.makeText(context, "Failed to load " + dataName + ".", Toast.LENGTH_LONG).show();
    }

    public void addButtons(Context context, List<String> coordinatesList) {
        ParkingData parkingData = new ParkingData();

//        parkingData.getCoordinates(coordinatesList -> {
            if (coordinatesList == null || coordinatesList.isEmpty()) {
                Toast.makeText(context, "No coordinates found", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!(map.getParent() instanceof ConstraintLayout)) {
                Toast.makeText(context, "Map is not inside a ConstraintLayout", Toast.LENGTH_SHORT).show();
                return;
            }

            ConstraintLayout layout = (ConstraintLayout) map.getParent();

            double centerLat = 21.3069; //For Honolulu
            double centerLng = -157.8583;
            int zoom = 13;
            int mapWidth = map.getWidth();
            int mapHeight = map.getHeight();
            int size = 80;

            for (String coord : coordinatesList) {
                try {
                    String[] latLng = coord.split(",");
                    double lat = Double.parseDouble(latLng[0].trim());
                    double lng = Double.parseDouble(latLng[1].trim());

                    Point p = CoordsToPixelsConverter.convertToPixels(
                            centerLat, centerLng,
                            lat, lng,
                            zoom,
                            mapWidth, mapHeight
                    );

                    int x = p.x;
                    int y = p.y;

                    Button button = new Button(context);
                    button.setAlpha(0f); //invisible
                    button.setLayoutParams(new ConstraintLayout.LayoutParams(size, size));
                    button.setTranslationX(x - size / 2f);
                    button.setTranslationY(y - size / 2f);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, StartParkingActivity.class);

                            if (context instanceof android.app.Activity) {
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Cannot start activity from this context", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    int finalX = x;
                    int finalY = y;

                    layout.addView(button);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }
}
