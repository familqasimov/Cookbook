package com.github.jnuutinen.cookbook.presentation.main;

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

class RecipeAdapter extends ArrayAdapter<Recipe> {

    RecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, 0, recipes);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Recipe recipe = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_recipe, parent, false);
            viewHolder.name = convertView.findViewById(R.id.text_recipe_name);
            viewHolder.category = convertView.findViewById(R.id.text_recipe_category);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (recipe != null) {
            viewHolder.name.setText(recipe.getName());
            if (recipe.getCategoryId() == null) {
                viewHolder.category.setText(null);
            } else {
                viewHolder.category.setText(null);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView category;
    }

}