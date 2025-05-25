package com.example.alohalotapp.admin;

public class ParkingSpace {
    public int Capacity;
    public int CordsX;
    public int CordsY;
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
}
