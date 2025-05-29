package com.example.alohalotapp;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserUsageStatsManager {

    private final String userId;
    private final DatabaseReference usageStatsRef;
    private final Map<String, Integer> usageStatsCache = new HashMap<>();

    public UserUsageStatsManager(String userId) {
        this.userId = userId;
        this.usageStatsRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("usageStats");
    }

    public interface OnUsageStatsLoadedListener {
        void onStatsLoaded(Map<String, Integer> usageStats);
    }

    // Φορτώνει τα usage stats από Firebase στον cache
    public void loadUsageStatsAsync(OnUsageStatsLoadedListener listener) {
        usageStatsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                usageStatsCache.clear();
                for (DataSnapshot child : task.getResult().getChildren()) {
                    String parkingName = child.getKey();
                    Integer count = child.getValue(Integer.class);
                    if (parkingName != null && count != null) {
                        usageStatsCache.put(parkingName, count);
                    }
                }
                Log.d("UserUsageStatsManager", "Loaded usageStats: " + usageStatsCache);
                listener.onStatsLoaded(new HashMap<>(usageStatsCache));
            } else {
                Log.w("UserUsageStatsManager", "No usage stats found");
                listener.onStatsLoaded(new HashMap<>());
            }
        });
    }

//    // Αύξηση με βάση το όνομα parking (βάζει ή ενημερώνει key = name)
//    public void incrementParkingUsage(String parkingName) {
//        int currentCount = usageStatsCache.getOrDefault(parkingName, 0);
//        int newCount = currentCount + 1;
//        usageStatsCache.put(parkingName, newCount);
//        Log.d("UserUsageStatsManager", "Writing usageStats for parkingName: " + parkingName + " with count: " + newCount);
//        usageStatsRef.child(parkingName).setValue(newCount)
//                .addOnSuccessListener(aVoid -> Log.d("UserUsageStatsManager", "Updated usage for " + parkingName + " to " + newCount))
//                .addOnFailureListener(e -> Log.e("UserUsageStatsManager", "Failed to update usage for " + parkingName, e));
//    }
//
//    // Αύξηση με βάση parkingId: διαβάζει το όνομα και καλεί incrementParkingUsage με το όνομα
//    public void incrementUsageById(String parkingId) {
//        DatabaseReference parkingNameRef = FirebaseDatabase.getInstance()
//                .getReference("parkingspaces")
//                .child(parkingId)
//                .child("name");
//
//        parkingNameRef.get().addOnSuccessListener(snapshot -> {
//            String name = snapshot.getValue(String.class);
//            if (name != null) {
//                incrementParkingUsage(name);
//            } else {
//                Log.e("UserUsageStatsManager", "Name not found for parkingId: " + parkingId);
//            }
//        }).addOnFailureListener(e ->
//                Log.e("UserUsageStatsManager", "Failed to fetch name for parkingId: " + parkingId, e)
//        );
//    }

    public int getUsageCount(String parkingName) {
        return usageStatsCache.getOrDefault(parkingName, 0);
    }

    public Map<String, Integer> getAllUsageStats() {
        return new HashMap<>(usageStatsCache);
    }
}
