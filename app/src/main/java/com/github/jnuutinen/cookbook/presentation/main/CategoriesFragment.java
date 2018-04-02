package com.github.jnuutinen.cookbook.presentation.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesFragment extends Fragment {

    private List<Category> categories;
    private CategoryAdapter adapter;
    private ExpandableListView categoryList;
    private AllRecipesFragment.RecipeFragmentListener listener;
    private TextView noCategoriesText;
    private List<String> stringCategories;

    public CategoriesFragment() {
    }

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //noinspection ConstantConditions
        MenuInflater menuInflater = getActivity().getMenuInflater();

        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        int type = ExpandableListView.getPackedPositionType(info.packedPosition);

        // Context menu for groups (categories)
        if (type ==  ExpandableListView.PACKED_POSITION_TYPE_GROUP) {

            // don't show context menu for 'no category' group
            String category = stringCategories
                    .get(ExpandableListView.getPackedPositionGroup(info.packedPosition));
            if (!category.equals(getResources().getString(R.string.recipe_no_category))) {
                menuInflater.inflate(R.menu.context_menu_categories, menu);
            }
        } else if (type == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
            // Context menu for children (recipes)
            menuInflater.inflate(R.menu.context_menu_recipe, menu);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        noCategoriesText = view.findViewById(R.id.text_no_categories);
        categoryList = view.findViewById(R.id.list_categories);
        categoryList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (listener != null) {
                listener.onRecipeSelected(parent.getExpandableListAdapter()
                        .getChild(groupPosition, childPosition).toString());
            }
            return false;
        });

        registerForContextMenu(categoryList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AllRecipesFragment.RecipeFragmentListener) {
            listener = (AllRecipesFragment.RecipeFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement RecipeFragmentListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        observe();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView
                    .ExpandableListContextMenuInfo)
                    item.getMenuInfo();
            String selectedRecipeName;
            switch (item.getItemId()) {
                case R.id.action_edit_category:
                    if (listener != null) {
                        String category = stringCategories
                                .get(ExpandableListView.getPackedPositionGroup(info.packedPosition));
                        for (Category c : categories) {
                            if (c.getName().equals(category)) {
                                listener.onCategoryEdit(c);
                            }
                        }
                    }
                    return true;
                case R.id.action_delete_category:
                    if (listener != null) {
                        String category = stringCategories
                                .get(ExpandableListView.getPackedPositionGroup(info.packedPosition));
                        for (Category c : categories) {
                            if (c.getName().equals(category)) {
                                listener.onCategoryDelete(c);
                            }
                        }
                    }
                    return true;
                case R.id.action_edit_recipe:
                    selectedRecipeName = adapter.getChild(ExpandableListView
                            .getPackedPositionGroup(info.packedPosition), ExpandableListView
                            .getPackedPositionChild(info.packedPosition)).toString();
                    listener.onRecipeEdit(selectedRecipeName);
                    return true;
                case R.id.action_share:
                    selectedRecipeName = adapter.getChild(ExpandableListView
                            .getPackedPositionGroup(info.packedPosition), ExpandableListView
                            .getPackedPositionChild(info.packedPosition)).toString();
                    listener.onRecipeShare(selectedRecipeName);
                    return true;
                case R.id.action_delete_recipe:
                    selectedRecipeName = adapter.getChild(ExpandableListView
                            .getPackedPositionGroup(info.packedPosition), ExpandableListView
                            .getPackedPositionChild(info.packedPosition)).toString();
                    listener.onRecipeDelete(selectedRecipeName);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return false;
    }

    private void observe() {
        //noinspection ConstantConditions
        MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getCategories().observe(this, data -> categories = data);

        viewModel.getCombinedRecipes().observe(this, data -> {
            if (data == null || data.size() == 0) {
                noCategoriesText.setVisibility(View.VISIBLE);
            } else {
                noCategoriesText.setVisibility(View.GONE);
            }
            stringCategories = new ArrayList<>();
            for (Category c : categories) {
                stringCategories.add(c.getName());
            }
            if (data != null && data.size() > 0) {
                stringCategories.add(getResources().getString(R.string.recipe_no_category));
            }
            Map<String, List<String>> categoryMap = new HashMap<>();
            String noCategory = getResources().getString(R.string.recipe_no_category);
            if (data != null) {
                for (CombineDao.combinedRecipe combined : data) {
                    if (categoryMap.containsKey(combined.categoryName)) {
                        categoryMap.get(combined.categoryName).add(combined.recipeName);
                    } else {
                        if (combined.categoryName == null) {
                            if (!categoryMap.containsKey(noCategory)) {
                                ArrayList<String> a = new ArrayList<>();
                                a.add(combined.recipeName);
                                categoryMap.put(noCategory, a);
                            } else {
                                categoryMap.get(noCategory).add(combined.recipeName);
                            }
                        } else {
                            ArrayList<String> a = new ArrayList<>();
                            a.add(combined.recipeName);
                            categoryMap.put(combined.categoryName, a);
                        }
                    }
                }
            }
            adapter = new CategoryAdapter(getContext(), stringCategories, categoryMap);
            categoryList.setAdapter(adapter);
        });
    }
}
