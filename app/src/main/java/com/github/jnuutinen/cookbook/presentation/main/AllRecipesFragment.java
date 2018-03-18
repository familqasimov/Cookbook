package com.github.jnuutinen.cookbook.presentation.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.Utils;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class AllRecipesFragment extends Fragment {
    private static final String TAG = AllRecipesFragment.class.getSimpleName();

    private RecipeAdapter adapter;
    private List<CombineDao.combinedRecipe> combinedRecipes;
    private RecipeFragmentListener listener;
    private TextView noRecipesText;
    private ListView recipeList;
    private List<Recipe> recipes;

    public AllRecipesFragment() {
    }

    public static AllRecipesFragment newInstance() {
        return new AllRecipesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        noRecipesText = view.findViewById(R.id.text_no_recipes);
        recipeList = view.findViewById(R.id.list_recipes);
        recipeList.setOnItemClickListener((parent, view1, position, id) -> {
            if (listener != null) {
                CombineDao.combinedRecipe combined = combinedRecipes.get(position);
                if (recipes != null) {
                    Recipe foundRecipe = null;
                    for (Recipe recipe : recipes) {
                        if (recipe.getName().equals(combined.recipeName)) {
                            foundRecipe = recipe;
                            break;
                        }
                    }
                    if (foundRecipe != null) {
                        listener.onRecipeSelected(foundRecipe);
                    }
                }
            }
        });

        registerForContextMenu(recipeList);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //noinspection ConstantConditions
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_recipe, menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(MainActivity.FILTER_RECIPE_ACTION);
        //noinspection ConstantConditions
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(filterReceiver, filter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecipeFragmentListener) {
            listener = (RecipeFragmentListener) context;
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
    public void onPause() {
        super.onPause();
        //noinspection ConstantConditions
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(filterReceiver);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //noinspection ConstantConditions
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(filterReceiver);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Recipe selectedRecipe = recipes.get(info.position);
            switch (item.getItemId()) {
                case R.id.action_edit_recipe:
                    listener.onRecipeEdit(selectedRecipe);
                    return true;
                case R.id.action_share:
                    listener.onRecipeShare(selectedRecipe);
                    return true;
                case R.id.action_delete_recipe:
                    listener.onRecipeDelete(selectedRecipe);
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
        return false;
    }

    private BroadcastReceiver filterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String filterString = intent.getStringExtra("filter");
            adapter.getFilter().filter(filterString);
        }
    };

    private void observe() {
        //noinspection ConstantConditions
        MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, data -> recipes = Utils.sortRecipesByName(data));
        viewModel.getCombinedRecipes().observe(this, data -> {
            if (data == null || data.size() == 0) {
                noRecipesText.setVisibility(VISIBLE);
            } else {
                noRecipesText.setVisibility(GONE);
            }
            data = Utils.sortCombinedRecipesByName(data);
            combinedRecipes = data;
            adapter = new RecipeAdapter(getContext(), data);
            recipeList.setAdapter(adapter);
        });
    }

    public interface RecipeFragmentListener {
        void onCategoryEdit(Category category);
        void onCategoryDelete(Category category);
        void onRecipeDelete(Recipe recipe);
        void onRecipeDelete(String name);
        void onRecipeEdit(Recipe recipe);
        void onRecipeEdit(String name);
        void onRecipeSelected(Recipe recipe);
        void onRecipeSelected(String name);
        void onRecipeShare(Recipe recipe);
        void onRecipeShare(String name);
    }
}
