package com.github.jnuutinen.cookbook.presentation.categories;


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

public class CategoryAdapter extends ArrayAdapter<Category> {

    CategoryAdapter(Context context, List<Category> categories) {
        super(context, 0, categories);
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        Category category = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_recipe, parent, false);
            viewHolder.name = convertView.findViewById(R.id.text_recipe_name);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (category != null) {
            viewHolder.name.setText(category.getName());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
    }
}
