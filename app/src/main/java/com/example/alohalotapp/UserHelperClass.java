package com.example.alohalotapp;

import java.util.HashMap;
import java.util.Map;

public class UserHelperClass {

    String regEmail, regPassword;
    Map<String, Integer> usageStats;  // key = parkingId, value = count

    public UserHelperClass() {
        usageStats = new HashMap<>();
    }

    public UserHelperClass(String regEmail, String regPassword) {
        this.regEmail = regEmail;
        this.regPassword = regPassword;
        this.usageStats = new HashMap<>();
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
