package com.example.alohalotapp;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.ViewHolder> {

    private final List<String> labels;    // List of legend labels (e.g. parking names)
    private final List<Integer> colors;   // Corresponding list of colors for each label
    private int selectedPosition = RecyclerView.NO_POSITION;  // Currently selected item position

    // Constructor receives lists of labels and colors
    public LegendAdapter(List<String> labels, List<Integer> colors) {
        this.labels = labels;
        this.colors = colors;
    }

    // Method to update which item is selected and notify RecyclerView to refresh those items
    public void setSelectedPosition(int position) {
        int oldPos = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPos);      // Refresh old selected item to remove highlight
        notifyItemChanged(position);    // Refresh new selected item to add highlight
    }

    // ViewHolder class holds the views for each legend item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        View colorBox;     // A view to show the color box
        TextView label;    // TextView to show the label text

        public ViewHolder(View itemView) {
            super(itemView);
            colorBox = itemView.findViewById(R.id.legend_color_box);
            label = itemView.findViewById(R.id.legend_label);
        }
    }

    // Inflates the layout for each legend item
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.legend_item, parent, false);
        return new ViewHolder(view);
    }

    // Binds data (label and color) to each item view and applies bold style if selected
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.label.setText(labels.get(position));
        holder.colorBox.setBackgroundColor(colors.get(position));

        // Make the label bold if this item is selected, normal otherwise
        if (position == selectedPosition) {
            holder.label.setTypeface(null, Typeface.BOLD);
        } else {
            holder.label.setTypeface(null, Typeface.NORMAL);
        }
    }

    // Returns the total number of legend items
    @Override
    public int getItemCount() {
        return labels.size();
    }
}
