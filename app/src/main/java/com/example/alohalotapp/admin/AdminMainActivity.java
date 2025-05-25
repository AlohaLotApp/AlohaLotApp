package com.example.alohalotapp.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.alohalotapp.R;

import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> parkingNames = new ArrayList<>();
    private FirebaseAdminHelperClass firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        listView = findViewById(R.id.listView);
        Button createNewButton = findViewById(R.id.createNewButton);
        firebaseHelper = new FirebaseAdminHelperClass();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, parkingNames);
        listView.setAdapter(arrayAdapter);

        // ✅ Fully delegate to helper
        firebaseHelper.loadParkingNames(
                names -> {
                    parkingNames.clear();
                    parkingNames.addAll(names);
                    arrayAdapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Failed to load data: " + error, Toast.LENGTH_SHORT).show()
        );

        createNewButton.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateNewParkingActivity.class));
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = parkingNames.get(position);
            Intent intent = new Intent(this, ModifyParkingActivity.class);
            intent.putExtra("parking_name", selectedName);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.adminmenu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }

            @Override public boolean onQueryTextChange(String newText) {
                arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
