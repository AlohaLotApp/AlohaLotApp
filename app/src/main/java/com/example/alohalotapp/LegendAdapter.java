package com.example.alohalotapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.ViewHolder> {

    private final List<String> labels;
    private final List<Integer> colors;

    public LegendAdapter(List<String> labels, List<Integer> colors) {
        this.labels = labels;
        this.colors = colors;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View colorBox;
        TextView label;

        public ViewHolder(View itemView) {
            super(itemView);
            colorBox = itemView.findViewById(R.id.legend_color_box);
            label = itemView.findViewById(R.id.legend_label);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.legend_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.label.setText(labels.get(position));
        holder.colorBox.setBackgroundColor(colors.get(position));
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }
}
