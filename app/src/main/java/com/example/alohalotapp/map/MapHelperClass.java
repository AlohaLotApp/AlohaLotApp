package com.example.alohalotapp.map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.alohalotapp.StartParkingActivity;
import com.example.alohalotapp.admin.FirebaseAdminHelperClass;
import com.squareup.picasso.Picasso;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

        FirebaseAdminHelperClass firebaseHelper = new FirebaseAdminHelperClass();
        ArrayList<String> openParkingSpotsCoordinatesList = new ArrayList<>();
        ArrayList<String> openParkingSpotsNamesList = new ArrayList<>();

        firebaseHelper.loadCoordinates(coordinatesList -> {
            firebaseHelper.loadCapacities(capacitiesList -> {
                firebaseHelper.loadCurrentUsers(currentUsersList -> {
                    firebaseHelper.loadIsHandicapped(isHandicappedList -> {
                        firebaseHelper.loadOpeningHours(openingHoursList -> {
                            firebaseHelper.loadParkingNames(parkingNamesList -> {

                                StringBuilder markerBuilder = new StringBuilder();
                                int size = Collections.min(Arrays.asList(
                                        coordinatesList.size(),
                                        capacitiesList.size(),
                                        currentUsersList.size(),
                                        isHandicappedList.size(),
                                        openingHoursList.size(),
                                        parkingNamesList.size()
                                ));

                                LocalTime now = LocalTime.now();

                                for (int i = 0; i < size; i++) {
                                    String color = "red"; // default

                                    try {
                                        LocalTime openingTime = LocalTime.parse(openingHoursList.get(i).first);
                                        LocalTime closingTime = LocalTime.parse(openingHoursList.get(i).second);

                                        boolean isOpen;
                                        if (closingTime.isAfter(openingTime)) {
                                            isOpen = !now.isBefore(openingTime) && now.isBefore(closingTime);
                                        } else {
                                            // Overnight case (e.g., 10:00 to 03:00 next day)
                                            isOpen = !now.isBefore(openingTime) || now.isBefore(closingTime);
                                        }

                                        if (!isOpen || capacitiesList.get(i).equals(currentUsersList.get(i))) {
                                            color = "black"; // closed or full
                                        } else if (Boolean.TRUE.equals(isHandicappedList.get(i))) {
                                            color = "blue";
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (!"black".equals(color)) {
                                        openParkingSpotsCoordinatesList.add(coordinatesList.get(i));
                                        openParkingSpotsNamesList.add(parkingNamesList.get(i));
                                    }

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
                                        addButtons(context, openParkingSpotsCoordinatesList, openParkingSpotsNamesList); // You could also pass parkingNamesList here
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Toast.makeText(context, "Failed to load map image", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }, error -> showError(context, "parking names"));
                        }, error -> showError(context, "opening hours"));
                    }, error -> showError(context, "handicapped info"));
                }, error -> showError(context, "current users"));
            }, error -> showError(context, "capacities"));
        }, error -> showError(context, "coordinates"));
    }
    private void showError(Context context, String dataName) {
        Toast.makeText(context, "Failed to load " + dataName + ".", Toast.LENGTH_LONG).show();
    }

    public void addButtons(Context context, List<String> coordinatesList, List<String> parkingNamesList) {
        if (coordinatesList == null || coordinatesList.isEmpty()) {
            Toast.makeText(context, "No coordinates found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!(map.getParent() instanceof ConstraintLayout)) {
            Toast.makeText(context, "Map is not inside a ConstraintLayout", Toast.LENGTH_SHORT).show();
            return;
        }

        ConstraintLayout layout = (ConstraintLayout) map.getParent();

        double centerLat = 21.3069; // For Honolulu
        double centerLng = -157.8583;
        int zoom = 13;
        int mapWidth = map.getWidth();
        int mapHeight = map.getHeight();
        int size = 80;

        for (int i = 0; i < coordinatesList.size(); i++) {
            try {
                String coordinates = coordinatesList.get(i);

                String[] latLng = coordinates.split(",");
                double lat = Double.parseDouble(latLng[0].trim());
                double lng = Double.parseDouble(latLng[1].trim());

                Point p = CoordinatesToPixelsConverter.convertToPixels(
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

                final String parkingNameFinal = (parkingNamesList != null && i < parkingNamesList.size())
                        ? parkingNamesList.get(i)
                        : "Unknown";

                button.setOnClickListener(view -> {
                    Intent intent = new Intent(context, StartParkingActivity.class);
                    intent.putExtra("parkingName", parkingNameFinal);

                    if (context instanceof android.app.Activity) {
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, "Cannot start activity from this context", Toast.LENGTH_SHORT).show();
                    }
                });

                layout.addView(button);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
