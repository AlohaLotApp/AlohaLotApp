package com.example.alohalotapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.WindowManager;

import com.example.alohalotapp.cards.Card;
import com.example.alohalotapp.db.CardDatabase;

import java.util.List;

public class EditCardsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private com.example.alohalotapp.cards.CardAdapter adapter;
    private CardDatabase db;
    private List<Card> cardList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_edit_cards);

        recyclerView = findViewById(R.id.cardRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = new CardDatabase(this);
        cardList = db.getAllCards();

        adapter = new com.example.alohalotapp.cards.CardAdapter(cardList, card -> {
            db.deleteCard(card.holder, card.number);
            cardList.remove(card);
            adapter.notifyDataSetChanged();
        });

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
