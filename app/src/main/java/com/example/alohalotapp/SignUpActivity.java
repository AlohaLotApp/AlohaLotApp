package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {

    // Αρχεία UI για το email, password και επιβεβαίωση password
    TextInputLayout regEmail, regPassword, regConfirm;
    // Κουμπί εγγραφής
    Button regBtn;

    // Αντικείμενα Firebase για τη βάση δεδομένων
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    // Μεταβλητή για αποθήκευση του μοναδικού userId μετά την εγγραφή
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Κάνει την οθόνη fullscreen, αφαιρεί status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Ορίζει το layout της δραστηριότητας
        setContentView(R.layout.activity_sign_up);

        // Συνδέει τις μεταβλητές με τα στοιχεία UI από το XML
        regEmail = findViewById(R.id.registerEmail);
        regPassword = findViewById(R.id.registerPassword);
        regConfirm = findViewById(R.id.registerConfPassword);
        regBtn = findViewById(R.id.SignUpBtn);

        // Ορίζει την ενέργεια που γίνεται όταν πατηθεί το κουμπί εγγραφής
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Αρχικοποιεί το Firebase Realtime Database με το URL της βάσης σου
                rootNode = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");
                // Παίρνει αναφορά στον κόμβο "users" όπου θα αποθηκεύονται οι χρήστες
                reference = rootNode.getReference("users");

                // Παίρνει το κείμενο που έγραψε ο χρήστης στα πεδία email, password, confirm password
                String email = regEmail.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                String confPassword = regConfirm.getEditText().getText().toString();

                //έλεγχος για το αν τα πεδία είναι συμπληρωμένα
                if (email.isEmpty()) {
                    regEmail.setError("Email is required");
                    return;
                } else {
                    regEmail.setError(null);
                }

                if (password.isEmpty()) {
                    regPassword.setError("Password is required");
                    return;
                } else {
                    regPassword.setError(null);
                }

                if (confPassword.isEmpty()) {
                    regConfirm.setError("Please confirm your password");
                    return;
                } else {
                    regConfirm.setError(null);
                }


                // Έλεγχος αν τα password ταιριάζουν
                if (!password.equals(confPassword)) {
                    // Αν όχι, βάζει μήνυμα λάθους στο confirm password
                    regConfirm.setError("Passwords do not match");
                    return; // Διακόπτει την εγγραφή
                } else {
                    // Αν ταιριάζουν, καθαρίζει τυχόν προηγούμενο λάθος
                    regConfirm.setError(null);
                }

                // Δημιουργεί ένα αντικείμενο UserHelperClass με το email και password για αποθήκευση
                UserHelperClass helperClass = new UserHelperClass(email, password, 0, 0.0, 0);

                //Παίρνει όλα τα δεδομένα από το "users" για να μετρήσει πόσοι χρήστες υπάρχουν ήδη
                reference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        long count = 0;
                        if (task.getResult().exists()) {
                            // Αποθηκεύει το πλήθος των παιδιών (χρηστών) που ήδη υπάρχουν
                            count = task.getResult().getChildrenCount();
                        }

                        // Δημιουργεί μοναδικό userId, πχ User1, User2, User3...
                        userId = "User" + (count + 1);

                        // Αποθηκεύει τον νέο χρήστη στη βάση στη θέση με key το userId
                        reference.child(userId).setValue(helperClass).addOnCompleteListener(saveTask -> {
                            if (saveTask.isSuccessful()) {
                                // Αποθηκεύει το userId τοπικά μέσω SessionManager για να το χρησιμοποιείς αλλού
                                SessionManager sessionManager = new SessionManager(SignUpActivity.this);
                                sessionManager.saveUserId(userId);

                                initializeUserUsageStats(userId);
                                initializeUserPaymentStats(userId);
                                initializeUserOrders(userId);

                                // Ξεκινάει την επόμενη δραστηριότητα (πχ αρχική οθόνη εφαρμογής)
                                Intent intent = new Intent(SignUpActivity.this, StartActivity.class);
                                startActivity(intent);
                                finish(); // Κλείνει την τρέχουσα δραστηριότητα
                            }
                        });
                    }
                });
            }
        });
    }

    private void initializeUserUsageStats(String userId) {
        DatabaseReference parkingsRef = rootNode.getReference("parkingspaces");
        DatabaseReference userUsageStatsRef = rootNode.getReference("users").child(userId).child("usageStats");

        parkingsRef.get().addOnCompleteListener(parkingTask -> {
            if (parkingTask.isSuccessful() && parkingTask.getResult().exists()) {
                Map<String, Object> allParkings = (Map<String, Object>) parkingTask.getResult().getValue();

                if (allParkings == null) return;

                Map<String, Integer> usageStats = new HashMap<>();

                for (Map.Entry<String, Object> entry : allParkings.entrySet()) {
                    Map<String, Object> parkingData = (Map<String, Object>) entry.getValue();
                    String parkingName = (String) parkingData.get("name");
                    if (parkingName != null) {
                        usageStats.put(parkingName, 0);
                    }
                }

                userUsageStatsRef.setValue(usageStats);
            }
        });
    }

    private void initializeUserPaymentStats(String userId) {
        DatabaseReference paymentStatsRef = rootNode.getReference("users").child(userId).child("paymentStats");

        Map<String, Integer> initialPaymentStats = new HashMap<>();
        initialPaymentStats.put("Paid3", 0);
        initialPaymentStats.put("Paid5", 0);
        initialPaymentStats.put("Paid11", 0);

        paymentStatsRef.setValue(initialPaymentStats);
    }

    private void initializeUserOrders(String userId){
        DatabaseReference userOrdersRef = rootNode.getReference("users").child(userId).child("orders");

        userOrdersRef.setValue(new HashMap<>());
    }



    // Getter μέθοδος για το userId που δημιουργήθηκε, αν χρειαστεί κάπου αλλού
    public String getUserId() {
        return userId;
    }
}
