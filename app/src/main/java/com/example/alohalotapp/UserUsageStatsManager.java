package com.example.alohalotapp;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserUsageStatsManager {
    private final String userId;
    private final DatabaseReference usageStatsRef;
    private Map<String, Integer> usageStatsCache = new HashMap<>();

    public UserUsageStatsManager(String userId) {
        this.userId = userId;
        usageStatsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("usageStats");
        loadUsageStats();
    }

    private void loadUsageStats() {
        usageStatsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                usageStatsCache.clear();
                for (com.google.firebase.database.DataSnapshot child : task.getResult().getChildren()) {
                    String parkingId = child.getKey();
                    Integer count = child.getValue(Integer.class);
                    if (parkingId != null && count != null) {
                        usageStatsCache.put(parkingId, count);
                    }
                }
                Log.d("UserUsageStatsManager", "Loaded usageStats: " + usageStatsCache);
            } else {
                Log.d("UserUsageStatsManager", "No usage stats found or failed to load.");
            }
        });
    }

    // Αυξάνει το count χρήσης για το συγκεκριμένο parkingId
    public void incrementParkingUsage(String parkingId) {
        int currentCount = usageStatsCache.getOrDefault(parkingId, 0);
        int newCount = currentCount + 1;
        usageStatsCache.put(parkingId, newCount);
        usageStatsRef.child(parkingId).setValue(newCount)
                .addOnSuccessListener(aVoid -> Log.d("UserUsageStatsManager", "Updated usage for " + parkingId + " to " + newCount))
                .addOnFailureListener(e -> Log.e("UserUsageStatsManager", "Failed to update usage for " + parkingId, e));
    }

    // Επιστρέφει πόσες φορές έχει παρκάρει σε συγκεκριμένο parking
    public int getUsageCount(String parkingId) {
        return usageStatsCache.getOrDefault(parkingId, 0);
    }

    // Επιστρέφει όλο το Map (προαιρετικό)
    public Map<String, Integer> getAllUsageStats() {
        return new HashMap<>(usageStatsCache);
    }

}
