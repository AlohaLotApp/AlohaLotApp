package com.example.alohalotapp;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Custom adapter for displaying a legend for the PieChart.
 * Each item consists of a color box and a label.
 */
public class LegendAdapter extends RecyclerView.Adapter<LegendAdapter.ViewHolder> {

    private final List<String> labels;  // List of parking names (or labels) for each slice
    private final List<Integer> colors; // Corresponding colors for each slice
    private int selectedPosition = RecyclerView.NO_POSITION; // Index of the currently selected item (if any)

    // Constructor
    public LegendAdapter(List<String> labels, List<Integer> colors) {
        this.labels = labels;
        this.colors = colors;
    }

    /**
     * Sets the selected position in the legend and refreshes the view.
     * @param position the index of the selected legend item
     */
    public void setSelectedPosition(int position) {
        int oldPos = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPos);     // Refresh old selected item
        notifyItemChanged(position);   // Refresh new selected item
    }

    // ViewHolder class that holds references to the UI components of each legend item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        View colorBox;       // View representing the color of the slice
        TextView label;      // Text label of the slice

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

    // Binds data (label and color) to each legend item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.label.setText(labels.get(position));                    // Set legend text
        holder.colorBox.setBackgroundColor(colors.get(position));      // Set background color

        // Apply bold text if this item is selected, otherwise normal
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
