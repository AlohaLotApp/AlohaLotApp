package com.example.alohalotapp.map;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.time.LocalTime;

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
        int width = map.getWidth();
        int height = map.getHeight();

        ParkingData parkingData = new ParkingData();

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

                            for (int i = 0; i < size; i++) {
                                String color = "red"; //default

                                LocalTime now = LocalTime.now();
                                LocalTime openingTime = LocalTime.parse(openingHoursList.get(i).first);

                                LocalTime closingTime = LocalTime.parse(openingHoursList.get(i).second);

                                boolean isOpen = !now.isBefore(openingTime) && now.isBefore(closingTime);

                                System.out.println("parking" + i + ":" + isOpen);

                                try {
                                    if (!isOpen || capacitiesList.get(i).equals(currentUsersList.get(i))) {
                                        color = "black";
                                    } else if (Boolean.TRUE.equals(isHandicappedList.get(i))) {
                                        color = "blue";
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
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

                            Picasso.get().load(mapUrl).into(map);

                        }, error -> {
                            Toast.makeText(context, "Failed to load opening hours.", Toast.LENGTH_LONG).show();
                        });

                    }, error -> {
                        Toast.makeText(context, "Failed to load handicapped info.", Toast.LENGTH_LONG).show();
                    });

                }, error -> {
                    Toast.makeText(context, "Failed to load current users.", Toast.LENGTH_LONG).show();
                });

            }, error -> {
                Toast.makeText(context, "Failed to load capacities.", Toast.LENGTH_LONG).show();
            });

        }, error -> {
            Toast.makeText(context, "Failed to load coordinates: " + error, Toast.LENGTH_LONG).show();
        });
    }
}
