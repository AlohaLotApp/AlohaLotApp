import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AppCompatActivity;

public class WalletActivity extends AppCompatActivity {

    private TextView balanceCountText;
    private int balance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_wallet);

        Button addBalanceBtn = findViewById(R.id.addBalanceBtn);
        balanceCountText = findViewById(R.id.balanceCount);

        addBalanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBalanceBottomSheet();
            }
        });
    }

    private void showBalanceBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(WalletActivity.this);
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.balance_bottom_sheet, null);
        bottomSheetDialog.setContentView(sheetView);

        Button option5 = sheetView.findViewById(R.id.option5);
        Button option10 = sheetView.findViewById(R.id.option10);
        Button option15 = sheetView.findViewById(R.id.option15);

        option5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBalance(5);
                bottomSheetDialog.dismiss();
            }
        });

        option10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBalance(10);
                bottomSheetDialog.dismiss();
            }
        });

        option15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateBalance(15);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void updateBalance(int amount) {
        balance += amount;
        balanceCountText.setText(balance + "€");
        Toast.makeText(this, "Added €" + amount, Toast.LENGTH_SHORT).show();
    }
}
