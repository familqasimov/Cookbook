package com.github.jnuutinen.cookbook.presentation.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;

import java.util.ArrayList;
import java.util.List;

class RecipeAdapter extends ArrayAdapter<CombineDao.combinedRecipe> implements Filterable {

    private List<CombineDao.combinedRecipe> combinedRecipes;
    private List<CombineDao.combinedRecipe> filtered;
    private ItemFilter filter = new ItemFilter();

    RecipeAdapter(Context context, List<CombineDao.combinedRecipe> recipes) {
        super(context, 0, recipes);
        combinedRecipes = recipes;
        filtered = recipes;
    }

    @Override
    public int getCount() {
        return filtered.size();
    }

    @Override
    public CombineDao.combinedRecipe getItem(int position) {
        return filtered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    @NonNull
    public Filter getFilter() {
        return filter;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        CombineDao.combinedRecipe recipe = getItem(position);

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
            viewHolder.name.setText(recipe.recipeName);
            if (recipe.categoryName == null) {
                viewHolder.category.setText(R.string.recipe_no_category);
            } else {
                viewHolder.category.setText(recipe.categoryName);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView category;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final List<CombineDao.combinedRecipe> original = combinedRecipes;
            int count = original.size();
            final ArrayList<CombineDao.combinedRecipe> newList =
                    new ArrayList<>(count);
            String filterableString;
            for (int i = 0; i < count; i++) {
                filterableString = original.get(i).recipeName;
                if (filterableString.toLowerCase().contains(filterString)) {
                    newList.add(original.get(i));
                }
                filterableString = original.get(i).categoryName;
                if (filterableString != null) {
                    if (filterableString.toLowerCase().contains(filterString)) {
                        if (!newList.contains(original.get(i))) {
                            newList.add(original.get(i));
                        }
                    }
                }
            }
            results.values = newList;
            results.count = newList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filtered = (ArrayList<CombineDao.combinedRecipe>) results.values;
            notifyDataSetChanged();
        }
    }

}