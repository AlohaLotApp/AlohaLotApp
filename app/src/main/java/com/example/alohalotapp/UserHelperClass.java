package com.example.alohalotapp;

import java.util.HashMap;
import java.util.Map;

public class UserHelperClass {

    //Has to be public for Firebase to work!
    public String regEmail, regPassword;
    public int points;

    public double amountSpent;

    public int totalParkings;

    public UserHelperClass() {
    }

    public UserHelperClass(String regEmail, String regPassword, int points, double amountSpent, int totalParkings) {
        this.regEmail = regEmail;
        this.regPassword = regPassword;
        this.points = points;
        this.amountSpent = amountSpent;
        this.totalParkings = totalParkings;
    }
}
