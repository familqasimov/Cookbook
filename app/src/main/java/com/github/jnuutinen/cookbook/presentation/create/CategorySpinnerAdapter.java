package com.github.jnuutinen.cookbook.presentation.create;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Category;

import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {
    private List<Category> categories;

    CategorySpinnerAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
        this.categories = categories;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView name = new TextView(getContext());
        name.setPadding(16, 16, 16, 16);
        name.setText(categories.get(position).getName());
        return name;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Get recipe item for this position
        Category category = getItem(position);

        // Check if an existing view is being reused, otherwise inflate view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {

            // No view to reuse, inflate new view
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_spinner_category, parent, false);
            viewHolder.name = convertView.findViewById(R.id.text_category_spinner);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        if (category != null) {
            viewHolder.name.setText(category.getName());
        }

        // Return the completed view to render on screen
        return convertView;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView name;
    }
}
