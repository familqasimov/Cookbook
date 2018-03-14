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
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesFragment extends Fragment {

    private CategoryFragmentListener mListener;
    private TextView noCategoriesText;
    private ExpandableListView categoryList;
    private List<Category> categories;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //noinspection ConstantConditions
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_categories, menu);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        noCategoriesText = view.findViewById(R.id.text_no_categories);
        categoryList = view.findViewById(R.id.list_categories);

        categoryList.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            if (mListener != null) {
                mListener.onRecipeSelected(parent.getExpandableListAdapter()
                        .getChild(groupPosition, childPosition).toString());
            }
            return false;
        });

        registerForContextMenu(categoryList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof CategoryFragmentListener) {
            mListener = (CategoryFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnRecipeSelectedListener");
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
        mListener = null;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView
                .ExpandableListContextMenuInfo)
                item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_edit_category:
                if (mListener != null) {
                    String category = stringCategories
                            .get(ExpandableListView.getPackedPositionGroup(info.packedPosition));
                    for (Category c : categories) {
                        if (c.getName().equals(category)) {
                            mListener.onCategoryEdit(c);
                        }
                    }
                }
                return true;
            case R.id.action_delete_category:
                if (mListener != null) {
                    String category = stringCategories
                            .get(ExpandableListView.getPackedPositionGroup(info.packedPosition));
                    for (Category c : categories) {
                        if (c.getName().equals(category)) {
                            mListener.onCategoryDelete(c);
                        }
                    }
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
            Map<String, List<String>> categoryMap = new HashMap<>();
            String noCategory = getResources().getString(R.string.recipe_no_category);
            if (data != null) {
                for (CombineDao.combinedRecipe combined : data) {
                    if (!stringCategories.contains(combined.categoryName)) {
                        if (combined.categoryName == null) {
                            if (!stringCategories.contains(noCategory)) {
                                stringCategories.add(noCategory);
                            }
                        } else {
                            stringCategories.add(combined.categoryName);
                        }
                    }
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

            categoryList.setAdapter(new CategoryAdapter(getContext(), stringCategories,
                    categoryMap));
        });
    }

    public interface CategoryFragmentListener {
        void onRecipeSelected(String name);
        void onCategoryEdit(Category category);
        void onCategoryDelete(Category category);
    }
}
