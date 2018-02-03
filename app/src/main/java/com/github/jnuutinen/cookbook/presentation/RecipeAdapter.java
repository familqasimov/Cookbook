package com.github.jnuutinen.cookbook.presentation;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView category;
    }

    public RecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, 0, recipes);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        // Get recipe item for this position
        Recipe recipe = getItem(position);

        // Check if an existing view is being reused, otherwise inflate view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {

            // No view to reuse, inflate new view
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_recipe, parent, false);
            viewHolder.name = convertView.findViewById(R.id.text_recipe_name);
            viewHolder.category = convertView.findViewById(R.id.text_recipe_category);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        if (recipe != null) { // TODO: idk it said recipe.getName may produce NullPointerException
            viewHolder.name.setText(recipe.getName());
            if (recipe.getCategoryId() == null) {
                viewHolder.category.setText("No category");
            } else {
                // TODO: set category name instead of id
                viewHolder.category.setText(recipe.getCategoryId());
            }
        }

        // Return the completed view to render on screen
        return convertView;
    }

}