package com.example.alohalotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatisticsActivity extends AppCompatActivity {

    private TextView usersEmailTextView;
    private TextView pointsTextView;
    private TextView totalSpentTextView;
    private TextView totalParkingTextView;
    private TextView restPointsTextView;
    private Button rewardButton;
    private ProgressBar pointsProgressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_statistics);

        usersEmailTextView = findViewById(R.id.statsImageTView);
        pointsTextView = findViewById(R.id.totalPointsTView);
        totalSpentTextView = findViewById(R.id.totalSpentTView);
        totalParkingTextView = findViewById(R.id.totalParksTView);
        rewardButton = findViewById(R.id.rewardBtn);
        restPointsTextView = findViewById(R.id.restPoints);
        pointsProgressBar = findViewById(R.id.progressBar);


        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserId();


        FirebaseDatabase database = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users").child(userId);

        userRef.get().addOnCompleteListener(task -> {
            String email = task.getResult().child("regEmail").getValue(String.class);
            Integer points = task.getResult().child("points").getValue(Integer.class);
            Double amountSpent = task.getResult().child("amountSpent").getValue(Double.class);
            Integer parkings = task.getResult().child("totalParkings").getValue(Integer.class);

            usersEmailTextView.setText(email);
            pointsTextView.setText(points.toString());
            totalSpentTextView.setText(amountSpent.toString() + "$");
            totalParkingTextView.setText(parkings.toString());

            pointsProgressBar.setProgress(points);

            int rest = calculateRestPoints(points);
            if(rest == 0){
                rewardButton.setVisibility(View.VISIBLE);
                rewardButton.setOnClickListener(view -> {

                    rewardButton.setVisibility(View.GONE);
                    pointsProgressBar.setProgress(0);
                    pointsTextView.setText("0");
                    userRef.child("points").setValue(0);

                    SharedPreferences prefs = getSharedPreferences("wallet_prefs", MODE_PRIVATE);
                    int currentBalance = prefs.getInt("balance", 0);
                    int rewardPoints = 5;

                    int newBalance = currentBalance + rewardPoints;

                    prefs.edit().putInt("balance", newBalance).apply();

                    Toast.makeText(this, "Added " + rewardPoints + "$ to your wallet!", Toast.LENGTH_SHORT).show();
                });
            }


            restPointsTextView.setText(String.valueOf(rest));
        });

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.stats);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(StatisticsActivity.this, StartActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.wallet) {
                startActivity(new Intent(StatisticsActivity.this, WalletActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.stats) {
                return true;
            }
            return false;
        });
    }

    private int calculateRestPoints(int currentPoints) {
        int rewardThreshold = 100;
        return rewardThreshold - currentPoints;
    }
}
