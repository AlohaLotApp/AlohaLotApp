package com.example.alohalotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;

import android.text.SpannableString;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.highlight.Highlight;



public class StatisticsActivity extends AppCompatActivity {

    //Variables
    private TextView usersEmailTextView, pointsTextView, totalSpentTextView, totalParkingTextView, restPointsTextView, topSpotTextView;
    private Button rewardButton;
    private ProgressBar pointsProgressBar;
    private PieChart pieChart;
    private BarChart paymentBarChart;


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
        pieChart = findViewById(R.id.PieChart);
        paymentBarChart = findViewById(R.id.BarChart);
        topSpotTextView = findViewById(R.id.topSpotTView);


        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getUserId();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference userRef = database.getReference("users").child(userId);

        // Φόρτωση στοιχείων χρήστη
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                String email = task.getResult().child("regEmail").getValue(String.class);
                Integer points = task.getResult().child("points").getValue(Integer.class);
                Double amountSpent = task.getResult().child("amountSpent").getValue(Double.class);
                Integer parkings = task.getResult().child("totalParkings").getValue(Integer.class);

                usersEmailTextView.setText(email);
                pointsTextView.setText(points != null ? points.toString() : "0");
                totalSpentTextView.setText(amountSpent != null ? amountSpent + "$" : "0$");
                totalParkingTextView.setText(parkings != null ? parkings.toString() : "0");
                pointsProgressBar.setProgress(points != null ? points : 0);

                int rest = calculateRestPoints(points != null ? points : 0);
                restPointsTextView.setText(String.valueOf(rest));

                if (rest == 0) {
                    rewardButton.setVisibility(Button.VISIBLE);
                    rewardButton.setOnClickListener(view -> {
                        rewardButton.setVisibility(Button.GONE);
                        pointsProgressBar.setProgress(0);
                        pointsTextView.setText("0");
                        userRef.child("points").setValue(0);

                        SharedPreferences prefs = getSharedPreferences("wallet_prefs", MODE_PRIVATE);
                        int currentBalance = prefs.getInt("balance_" + userId, 0);
                        int rewardPoints = 5;
                        prefs.edit().putInt("balance_" + userId, currentBalance + rewardPoints).apply();

                        Toast.makeText(this, "Added " + rewardPoints + "$ to your wallet!", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });

        // Φόρτωση usageStats και εμφάνιση σε PieChart
        DatabaseReference usageStatsRef = database.getReference("users").child(userId).child("usageStats");
        // Φόρτωση usageStats πρώτα
        usageStatsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Map<String, Integer> usageStats = new HashMap<>();
                for (com.google.firebase.database.DataSnapshot child : task.getResult().getChildren()) {
                    String parkingId = child.getKey();
                    Integer countLong = child.getValue(Integer.class);
                    int count = countLong != null ? countLong.intValue() : 0;
                    if (parkingId != null) {
                        usageStats.put(parkingId, count);
                    }
                }

                // Φόρτωση ονομάτων parkingspaces
                DatabaseReference parkingRef = database.getReference("parkingspaces");
                parkingRef.get().addOnCompleteListener(parkingTask -> {
                    Map<String, String> parkingNamesMap = new HashMap<>();

                    if (parkingTask.isSuccessful() && parkingTask.getResult().exists()) {
                        for (com.google.firebase.database.DataSnapshot parkingSnapshot : parkingTask.getResult().getChildren()) {
                            String id = parkingSnapshot.getKey();
                            String name = parkingSnapshot.child("name").getValue(String.class);
                            if (id != null && name != null) {
                                parkingNamesMap.put(id, name);
                            }
                        }
                    } else {
                        Toast.makeText(this, "No parking names found", Toast.LENGTH_SHORT).show();
                    }

                    // Τώρα έχεις usageStats και parkingNamesMap → κάλεσε setupPieChart
                    setupPieChart(usageStats, parkingNamesMap);
                });
            } else {
                Toast.makeText(this, "No usage stats found", Toast.LENGTH_SHORT).show();
                pieChart.clear();
            }
        });

        loadPaymentStatsAndShowBarChart(database, userId);

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

    private void loadPaymentStatsAndShowBarChart(FirebaseDatabase database, String userId) {
        DatabaseReference paymentStatsRef = database.getReference("users").child(userId).child("paymentStats");

        paymentStatsRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Map<String, Long> paymentStats = (Map<String, Long>) task.getResult().getValue();

                List<BarEntry> entries = new ArrayList<>();

                // Νέα κλειδιά από τη βάση
                String[] paymentKeys = {"Paid3", "Paid5", "Paid11"};
                // Εμφανιζόμενα labels στον άξονα
                String[] paymentLabels = {"3", "5", "11"};

                for (int i = 0; i < paymentKeys.length; i++) {
                    long count = 0;
                    if (paymentStats != null && paymentStats.containsKey(paymentKeys[i])) {
                        count = paymentStats.get(paymentKeys[i]);
                    }
                    entries.add(new BarEntry(i, count));
                }

                BarDataSet dataSet = new BarDataSet(entries, "Payments");

// Ορισμός διαφορετικών χρωμάτων για κάθε μπάρα
                dataSet.setColors(new int[]{
                        ContextCompat.getColor(this, R.color.color1),
                        ContextCompat.getColor(this, R.color.color2),
                        ContextCompat.getColor(this, R.color.color3)
                });

                BarData barData = new BarData(dataSet);
                barData.setBarWidth(0.5f); // μία μπάρα ανά θέση

                paymentBarChart.setData(barData);
                barData.setValueTextSize(16f); // ή μεγαλύτερο π.χ. 18f, 20f ανάλογα με το πόσο μεγάλες θέλεις τις τιμές
                barData.setValueTypeface(Typeface.DEFAULT_BOLD); // προαιρετικά, για πιο έντονα νούμερα
                barData.setValueTextColor(Color.BLACK); // ή άλλο χρώμα που φαίνεται καλά
                paymentBarChart.setFitBars(true);
                paymentBarChart.getDescription().setEnabled(false);

                XAxis xAxis = paymentBarChart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setLabelCount(paymentLabels.length);
                xAxis.setDrawGridLines(false);
                xAxis.setDrawAxisLine(true);

// Κεντράρισμα των labels κάτω από τις μπάρες
                xAxis.setAxisMinimum(-0.5f);
                xAxis.setAxisMaximum(paymentLabels.length - 0.5f);
                barData.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value); // αφαιρεί τα δεκαδικά
                    }
                });

                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if (index >= 0 && index < paymentLabels.length) {
                            return paymentLabels[index] + "$";
                        } else {
                            return "";
                        }
                    }
                });

                xAxis.setTextSize(14f);


// Y Axis ρυθμίσεις
                paymentBarChart.getAxisLeft().setGranularity(1f);
                paymentBarChart.getAxisLeft().setGranularityEnabled(true);
                paymentBarChart.getAxisRight().setEnabled(false);

                paymentBarChart.invalidate();

            } else {
                paymentBarChart.clear();
            }
        });
    }



    private void setupPieChart(Map<String, Integer> usageStats, Map<String, String> parkingNamesMap) {
        pieChart.clear(); // Καθαρίζει το παλιό data

        SpannableString centerText = new SpannableString("Your Parking\nStats");

        // Bold στην πρώτη γραμμή
        centerText.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 12, 0);
        // Μεγαλύτερο μέγεθος για τη δεύτερη γραμμή
        centerText.setSpan(new android.text.style.RelativeSizeSpan(1.3f), 13, centerText.length(), 0);
        // Κεντραρισμένο κείμενο (προαιρετικό)
        centerText.setSpan(new android.text.style.AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, centerText.length(), 0);

        pieChart.setCenterText(centerText);
        pieChart.setCenterTextColor(Color.DKGRAY);

        List<PieEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : usageStats.entrySet()) {
            String parkingId = entry.getKey();
            int value = entry.getValue() != null ? entry.getValue() : 0;

            if (value == 0) continue;  // Παράλειψη κομματιών με 0

            String displayName = parkingNamesMap.getOrDefault(parkingId, parkingId);

            entries.add(new PieEntry(value, displayName));
            labels.add(displayName);
        }

        // Δημιουργία του dataset
        PieDataSet dataSet = new PieDataSet(entries, "Parking Usage");

        // Χρώματα για το chart και την custom legend
        List<Integer> colors = generateColors(entries.size());
        dataSet.setColors(colors);

        PieData pieData = new PieData(dataSet);
        pieData.setDrawValues(true);
        pieData.setValueTextSize(16f); // 👈 μεγαλώνει το μέγεθος
        pieData.setValueTextColor(Color.WHITE); // 👈 αν χρειάζεται για ορατότητα
        pieData.setValueTypeface(Typeface.DEFAULT_BOLD); // 👈 προαιρετικά bold

        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        pieChart.setData(pieData);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e instanceof PieEntry) {
                    PieEntry entry = (PieEntry) e;
                    String label = entry.getLabel();
                    float value = entry.getValue();

                    // Εδώ μπορείς να εμφανίσεις Toast ή να πας σε άλλη οθόνη
                    Toast.makeText(getApplicationContext(), label + ": " + (int)value, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected() {
                // Optional: όταν ο χρήστης πατάει σε κενό χώρο
            }
        });


        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        RecyclerView legendRecyclerView = findViewById(R.id.legendRecyclerView);
        legendRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        LegendAdapter adapter = new LegendAdapter(labels, colors);
        legendRecyclerView.setAdapter(adapter);

        String maxParkingName = findTopSpot(usageStats, parkingNamesMap);
        topSpotTextView.setText(maxParkingName);

        pieChart.invalidate();
    }


    private List<Integer> generateColors(int count) {
        List<Integer> colors = new ArrayList<>();
        float saturation = 0.7f; // κορεσμός (0-1)
        float lightness = 0.6f;  // φωτεινότητα (0-1)

        for (int i = 0; i < count; i++) {
            float hue = (360f / count) * i;  // Ομοιόμορφη κατανομή στον χρωματικό κύκλο
            int color = Color.HSVToColor(new float[]{hue, saturation, lightness});
            colors.add(color);
        }
        return colors;
    }


    private int calculateRestPoints(int currentPoints) {
        int rewardThreshold = 100;
        return Math.max(rewardThreshold - currentPoints, 0);
    }

    private String findTopSpot(Map<String, Integer> usageStats, Map<String, String> parkingNamesMap){
        String maxKey = null;
        int maxValue = Integer.MIN_VALUE;

        for (Map.Entry<String, Integer> entry : usageStats.entrySet()) {
            int value = entry.getValue() != null ? entry.getValue() : 0;
            if (value > maxValue) {
                maxValue = value;
                maxKey = entry.getKey();
            }
        }

        if (maxKey == null) {
            return null; // Δεν υπάρχουν στοιχεία
        }

        return parkingNamesMap.getOrDefault(maxKey, maxKey);
    }
}
