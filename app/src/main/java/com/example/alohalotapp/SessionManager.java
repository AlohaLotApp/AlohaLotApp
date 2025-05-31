package com.example.alohalotapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    // The name of the SharedPreferences file where user data is stored
    private static final String PREF_NAME = "UserSessionPrefs";
    // The key used to store/retrieve the userId
    private static final String KEY_USER_ID = "userId";

    // SharedPreferences object used to read stored data
    private SharedPreferences sharedPreferences;
    // Editor used to make changes to SharedPreferences (e.g., save new data)
    private SharedPreferences.Editor editor;

    // Constructor that receives a context from the Activity/Service
    public SessionManager(Context context) {
        // Initializes SharedPreferences with the name PREF_NAME and private mode (only accessible by the app)
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Initializes the editor for writing data
        editor = sharedPreferences.edit();
    }

    // Saves the userId into SharedPreferences
    public void saveUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);  // Stores the userId with the key KEY_USER_ID
        editor.apply(); // Applies the change asynchronously (non-blocking for UI thread)
    }

    // Retrieves the stored userId from SharedPreferences
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
        // Returns the userId if it exists, otherwise null
    }

    // Returns true if a userId is stored, meaning the user is considered "logged in"
    public boolean isLoggedIn() {
        return getUserId() != null;
    }

    // Clears all stored session data (e.g., during logout)
    public void clearSession() {
        editor.clear();  // Deletes all keys and values from SharedPreferences
        editor.apply();  // Applies the changes asynchronously
    }
}
