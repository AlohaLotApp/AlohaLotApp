package com.example.alohalotapp.admin;

public class ParkingSpace {
    public int capacity;
    public double latitude;
    public double longitude;
    public int currentUsers;

    private String openTime;
    private String closeTime;

    public String name;

    public boolean handicapped;

    public ParkingSpace() {
        // Required empty constructor for Firebase
    }

    public ParkingSpace(int capacity, double latitude, double longitude, int occupancy, String name, String openTime, String closeTime , boolean handicapped) {
        this.capacity = capacity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentUsers = occupancy;
        this.name = name;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.handicapped = handicapped;
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

    public String getOpenTime() { return openTime; }

    public String getCloseTime() { return closeTime; }

    public String getName() {
        return name;
    }

    public boolean getHandicapped() {return handicapped; }
}
