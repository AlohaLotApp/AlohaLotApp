package com.example.alohalotapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WalletActivity extends AppCompatActivity {

    private TextView balanceCountText;
    private int balance = 0;
    private SessionManager sessionManager;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wallet);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.wallet);

        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();

        if (userId == null) {
            // Optional: handle error if user is not logged in
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(WalletActivity.this, StartActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.wallet) {
                return true;
            } else if (id == R.id.stats) {
                startActivity(new Intent(WalletActivity.this, StatisticsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        balanceCountText = findViewById(R.id.balanceCount);
        loadBalance();

        Button addBalanceBtn = findViewById(R.id.addBalanceBtn);
        Button addCardsBtn = findViewById(R.id.addCardsBtn);
        Button editCardsBtn = findViewById(R.id.editCardsBtn);

        addBalanceBtn.setOnClickListener(view -> showBalanceOptions(view));
        addCardsBtn.setOnClickListener(view ->
                startActivity(new Intent(WalletActivity.this, AddCardActivity.class)));
        editCardsBtn.setOnClickListener(view ->
                startActivity(new Intent(WalletActivity.this, EditCardsActivity.class)));
    }

    private void showBalanceOptions(View anchorView) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.balance_popup, null);

        // Set height to 100dp
        int popupHeight = (int) (100 * getResources().getDisplayMetrics().density);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                popupHeight,
                true);

        Button option5 = popupView.findViewById(R.id.option5);
        Button option10 = popupView.findViewById(R.id.option10);
        Button option15 = popupView.findViewById(R.id.option15);

        View.OnClickListener listener = v -> {
            int amount = 0;
            switch (v.getId()) {
                case R.id.option5:
                    amount = 5;
                    break;
                case R.id.option10:
                    amount = 10;
                    break;
                case R.id.option15:
                    amount = 15;
                    break;
            }
            updateBalance(amount);
            popupWindow.dismiss();
        };

        option5.setOnClickListener(listener);
        option10.setOnClickListener(listener);
        option15.setOnClickListener(listener);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(10);
        popupWindow.showAsDropDown(anchorView);
    }

    private void updateBalance(int amount) {
        balance += amount;
        balanceCountText.setText(balance + "$");
        saveBalance();
        Toast.makeText(this, "Added $" + amount, Toast.LENGTH_SHORT).show();
    }

    private void loadBalance() {
        SharedPreferences prefs = getSharedPreferences("wallet_prefs", MODE_PRIVATE);
        balance = prefs.getInt("balance_" + userId, 0);
        balanceCountText.setText(balance + "$");
    }

    private void saveBalance() {
        getSharedPreferences("wallet_prefs", MODE_PRIVATE)
                .edit()
                .putInt("balance_" + userId, balance)
                .apply();
    }
}
