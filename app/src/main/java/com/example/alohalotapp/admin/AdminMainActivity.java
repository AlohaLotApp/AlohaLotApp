package com.example.alohalotapp.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.alohalotapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity {

    private ListView listView;
    private Button createNewButton;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> parkingNames = new ArrayList<>();

    // ✅ From wallet branch
    private DatabaseReference parkingRef;

    // ✅ From master branch
    private FirebaseAdminHelperClass firebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        listView = findViewById(R.id.listView);
        createNewButton = findViewById(R.id.createNewButton);
        firebaseHelper = new FirebaseAdminHelperClass();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, parkingNames);
        listView.setAdapter(arrayAdapter);

        // ✅ Option 1: Load using Firebase helper class (master branch)
        firebaseHelper.loadParkingNames(
                names -> {
                    parkingNames.clear();
                    parkingNames.addAll(names);
                    arrayAdapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Failed to load data: " + error, Toast.LENGTH_SHORT).show()
        );

        // ✅ Option 2: Load using direct Firebase reference (wallet branch)
        parkingRef = FirebaseDatabase.getInstance("https://alohalot-e2fd9-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference("ParkingSpaces");

        parkingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                parkingNames.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    String name = childSnapshot.child("name").getValue(String.class);
                    if (name != null) {
                        parkingNames.add(name);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminMainActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        createNewButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMainActivity.this, CreateNewParkingActivity.class);
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = parkingNames.get(position);
            Intent intent = new Intent(AdminMainActivity.this, ModifyParkingActivity.class);
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
