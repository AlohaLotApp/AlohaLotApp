package com.example.alohalotapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WalletActivity extends AppCompatActivity {

    private TextView balanceCountText;
    private int balance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wallet);

        balanceCountText = findViewById(R.id.balanceCount);
        loadBalance();

        Button addBalanceBtn = findViewById(R.id.addBalanceBtn);
        Button addCardsBtn = findViewById(R.id.addCardsBtn);
        Button editCardsBtn = findViewById(R.id.editCardsBtn);

        addBalanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBalanceOptions(view);
            }
        });

        addCardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalletActivity.this, AddCardActivity.class);
                startActivity(intent);
            }
        });

        editCardsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WalletActivity.this, EditCardsActivity.class);
                startActivity(intent);
            }
        });

    }

    private void showBalanceOptions(View anchorView) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.balance_popup, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true);

        Button option5 = popupView.findViewById(R.id.option5);
        Button option10 = popupView.findViewById(R.id.option10);
        Button option15 = popupView.findViewById(R.id.option15);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        };

        option5.setOnClickListener(listener);
        option10.setOnClickListener(listener);
        option15.setOnClickListener(listener);

        popupWindow.setOutsideTouchable(true);
        popupWindow.setElevation(10);

        popupWindow.showAsDropDown(anchorView);
    }

    // Update the Balance value
    private void updateBalance(int amount) {
        balance += amount;
        balanceCountText.setText(balance + "€");
        saveBalance();
        Toast.makeText(this, "Added €" + amount, Toast.LENGTH_SHORT).show();
    }


    private void loadBalance() {
        balance = getSharedPreferences("wallet_prefs", MODE_PRIVATE).getInt("balance", 0);
        balanceCountText.setText(balance + "€");
    }

    private void saveBalance() {
        getSharedPreferences("wallet_prefs", MODE_PRIVATE)
                .edit()
                .putInt("balance", balance)
                .apply();
    }


}
