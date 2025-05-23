package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.widget.SearchView;


public class AdminMainActivity extends AppCompatActivity {

    ListView listView;
    String[] names = {
            "Parking1", "Parking2", "Parking3", "Parking4", "Parking5",
            "Parking6", "Parking7", "Parking8", "Parking9", "Parking10",
            "Parking11", "Parking12", "Parking13", "Parking14", "Parking15",
            "Parking16", "Parking17", "Parking18", "Parking19", "Parking20"
    };

    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        listView = findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(this , android.R.layout.simple_dropdown_item_1line , names);
        listView.setAdapter(arrayAdapter);


        Button createNewButton = findViewById(R.id.createNewButton);
        createNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this,CreateNewParkingActivity.class);
                startActivity(intent);
            }
        });

        // Handle item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = names[position];

                Intent intent = new Intent(AdminMainActivity.this, ModifyParkingActivity.class);
                intent.putExtra("parking_name", selectedName);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.adminmenu , menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                arrayAdapter.getFilter().filter(newText);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }
}