package com.example.alohalotapp.admin;

public class ParkingSpace {
    public int Capacity;
    public int CordsX;
    public int CordsY;
    public double latitude;
    public double longitude;
    public int CurrentUsers;
    public String Name;

    public ParkingSpace() {
    }

    public ParkingSpace(int capacity, int cordsX, int cordsY, int currentUsers, String name) {
        this.Capacity = capacity;
        this.CordsX = cordsX;
        this.CordsY = cordsY;
        this.CurrentUsers = currentUsers;
        this.Name = name;
    }

    public ParkingSpace(int capacity, double latitude, double longitude, int currentUsers, String name) {
        this.Capacity = capacity;
        this.latitude = latitude;
        this.longitude = longitude;
        this.CurrentUsers = currentUsers;
        this.Name = name;
    }

    public String getName() {
        return this.Name;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public int getCapacity() {
        return this.Capacity;
    }
}
