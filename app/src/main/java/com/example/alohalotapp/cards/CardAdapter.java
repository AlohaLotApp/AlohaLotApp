package com.example.alohalotapp.cards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alohalotapp.R;
import com.example.alohalotapp.cards.Card;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Card card);
    }

    private List<Card> cardList;
    private OnDeleteClickListener listener;

    public CardAdapter(List<Card> cardList, OnDeleteClickListener listener) {
        this.cardList = cardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Card card = cardList.get(position);
        holder.tvHolder.setText(card.holder);
        holder.tvNumber.setText(card.number);
        holder.tvExpiry.setText(card.expiry);
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(card));
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvHolder, tvNumber, tvExpiry;
        Button btnDelete;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHolder = itemView.findViewById(R.id.tvCardHolder);
            tvNumber = itemView.findViewById(R.id.tvCardNumber);
            tvExpiry = itemView.findViewById(R.id.tvCardExpiry);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
