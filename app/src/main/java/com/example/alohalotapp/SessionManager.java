package com.example.alohalotapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    // Το όνομα του αρχείου SharedPreferences όπου αποθηκεύονται τα δεδομένα του χρήστη
    private static final String PREF_NAME = "UserSessionPrefs";
    // Το κλειδί με το οποίο αποθηκεύουμε/ανακτούμε το userId
    private static final String KEY_USER_ID = "userId";

    // Αντικείμενο SharedPreferences για ανάγνωση αποθηκευμένων δεδομένων
    private SharedPreferences sharedPreferences;
    // Editor για να κάνουμε αλλαγές στα SharedPreferences (π.χ. αποθήκευση νέων δεδομένων)
    private SharedPreferences.Editor editor;

    // Κατασκευαστής της κλάσης, λαμβάνει context από το Activity/Service
    public SessionManager(Context context) {
        // Αρχικοποιεί το SharedPreferences με το όνομα PREF_NAME και λειτουργία private (μόνο η εφαρμογή έχει πρόσβαση)
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        // Αρχικοποιεί τον editor για να μπορούμε να γράφουμε δεδομένα
        editor = sharedPreferences.edit();
    }

    // Αποθηκεύει το userId στα SharedPreferences
    public void saveUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);  // Αποθηκεύει το userId με το κλειδί KEY_USER_ID
        editor.apply(); // Εφαρμόζει την αλλαγή ασύγχρονα (χωρίς να μπλοκάρει το UI thread)
    }

    // Ανακτά το αποθηκευμένο userId από τα SharedPreferences
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
        // Επιστρέφει το userId αν υπάρχει, αλλιώς null
    }

    // Επιστρέφει true αν υπάρχει αποθηκευμένο userId, δηλαδή αν ο χρήστης θεωρείται "συνδεδεμένος"
    public boolean isLoggedIn() {
        return getUserId() != null;
    }

    // Καθαρίζει όλα τα αποθηκευμένα δεδομένα session (π.χ. σε logout)
    public void clearSession() {
        editor.clear();  // Διαγράφει όλα τα κλειδιά και τιμές στα SharedPreferences
        editor.apply();  // Εφαρμόζει τις αλλαγές ασύγχρονα
    }
}
