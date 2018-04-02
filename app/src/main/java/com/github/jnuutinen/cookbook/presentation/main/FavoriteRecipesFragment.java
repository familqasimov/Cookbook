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
import android.widget.ListView;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.Utils;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class FavoriteRecipesFragment extends Fragment {

    private List<CombineDao.combinedRecipe> combinedRecipes;
    private AllRecipesFragment.RecipeFragmentListener listener;
    private TextView noFavoritesText;
    private ListView recipeList;
    private List<Recipe> favoriteRecipes;

    public FavoriteRecipesFragment() {
    }


    public static FavoriteRecipesFragment newInstance() {
        return new FavoriteRecipesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        noFavoritesText = view.findViewById(R.id.text_no_favorites);
        recipeList = view.findViewById(R.id.list_favorites);
        recipeList.setOnItemClickListener((parent, view1, position, id) -> {
            if (listener != null) {
                CombineDao.combinedRecipe combined = combinedRecipes.get(position);
                if (favoriteRecipes != null) {
                    Recipe foundRecipe = null;
                    for (Recipe recipe : favoriteRecipes) {
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
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            Recipe selectedRecipe = favoriteRecipes.get(info.position);
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

    private void observe() {
        //noinspection ConstantConditions
        MainViewModel viewModel = ViewModelProviders.of(getActivity()).get(MainViewModel.class);
        viewModel.getFavoriteRecipes().observe(this, data -> favoriteRecipes = Utils.sortRecipesByName(data));
        viewModel.getFavoriteCombinedRecipes().observe(this, data -> {
            if (data == null || data.size() == 0) {
                noFavoritesText.setVisibility(VISIBLE);
            } else {
                noFavoritesText.setVisibility(GONE);
            }
            data = Utils.sortCombinedRecipesByName(data);
            combinedRecipes = data;
            recipeList.setAdapter(new RecipeAdapter(getContext(), data));
        });
    }

}
