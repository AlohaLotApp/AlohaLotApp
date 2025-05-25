package com.example.alohalotapp.admin;

public class ParkingSpace {
    public int capacity;
    public double latitude;
    public double longitude;
    public int currentUsers;
    public String name;

    public ParkingSpace() {
        // Required empty constructor for Firebase
    }

    public ParkingSpace(int capacity, double latitude, double longitude, int currentUsers, String name) {
        this.capacity = capacity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentUsers = currentUsers;
        this.name = name;
    }

    // Getters
    public int getCapacity() {
        return capacity;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCurrentUsers() {
        return currentUsers;
    }

    public String getName() {
        return name;
    }
}
