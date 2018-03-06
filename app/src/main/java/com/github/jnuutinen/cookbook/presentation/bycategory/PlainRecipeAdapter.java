package com.github.jnuutinen.cookbook.presentation.bycategory;

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

public class PlainRecipeAdapter extends ArrayAdapter<Recipe> {

    PlainRecipeAdapter(Context context, List<Recipe> recipes) {
        super(context, 0, recipes);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Recipe recipe = getItem(position);

        PlainRecipeAdapter.ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new PlainRecipeAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_plain_recipe, parent, false);
            viewHolder.name = convertView.findViewById(R.id.text_recipe_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PlainRecipeAdapter.ViewHolder) convertView.getTag();
        }
        if (recipe != null) {
            viewHolder.name.setText(recipe.getName());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
    }
}
