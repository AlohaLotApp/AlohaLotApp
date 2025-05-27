package com.example.alohalotapp;

import java.util.HashMap;
import java.util.Map;

public class UserHelperClass {

    String regEmail, regPassword;
    Map<String, Integer> usageStats;
    int points;

    double amountSpent;

    int totalParkings;

    public UserHelperClass() {
    }

    public UserHelperClass(String regEmail, String regPassword, int points, double amountSpent, int totalParkings) {
        this.regEmail = regEmail;
        this.regPassword = regPassword;
        this.usageStats = new HashMap<>();
        this.points = points;
        this.amountSpent = amountSpent;
        this.totalParkings = totalParkings;
    }

    public int getTotalParkings() {
        return totalParkings;
    }

    public void setTotalParkings(int totalParkings) {
        this.totalParkings = totalParkings;
    }

    public double getAmountSpent() {
        return amountSpent;
    }

    public void setAmountSpent(double amountSpent) {
        this.amountSpent = amountSpent;
    }


    // getter και setter για το points
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getRegEmail() {
        return regEmail;
    }

    public void setRegEmail(String regEmail) {
        this.regEmail = regEmail;
    }

    public String getRegPassword() {
        return regPassword;
    }

    public void setRegPassword(String regPassword) {
        this.regPassword = regPassword;
    }

    public Map<String, Integer> getUsageStats() { return usageStats; }

    public void setUsageStats(Map<String, Integer> usageStats) { this.usageStats = usageStats; }

    public void recordParkingUsage(String parkingId) {
        int count = usageStats.getOrDefault(parkingId, 0);
        usageStats.put(parkingId, count + 1);
    }
}
