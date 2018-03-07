package com.github.jnuutinen.cookbook.presentation.favorites;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.RecipeAdapter;
import com.github.jnuutinen.cookbook.presentation.Utils;
import com.github.jnuutinen.cookbook.presentation.viewrecipe.ViewRecipeActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

import static com.github.jnuutinen.cookbook.presentation.main.MainActivity.REQUEST_VIEW_RECIPE;

public class FavoritesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.text_no_favorites) TextView noFavoritesText;
    @BindView(R.id.list_favorites) ListView favoritesList;

    private RecipeAdapter adapter;
    private List<CombineDao.combinedRecipe> combinedRecipes;
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        observe();
    }

    @OnItemClick(R.id.list_favorites)
    void viewRecipe(int position) {
        Intent intent = new Intent(this, ViewRecipeActivity.class);
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
                intent.putExtra("recipe", foundRecipe);
                startActivityForResult(intent, REQUEST_VIEW_RECIPE);
            }
        }
    }

    private void observe() {
        ViewModelProviders.of(this).get(FavoritesViewModel.class)
                .getFavoriteCombinedRecipes().observe(this, data -> {
                    data = Utils.sortByName(data);
                    combinedRecipes = data;
                    adapter = new RecipeAdapter(this, data);
                    favoritesList.setAdapter(adapter);
                    if (data == null) {
                        noFavoritesText.setVisibility(View.VISIBLE);
                        favoritesList.setVisibility(View.GONE);
                    } else if (data.size() == 0) {
                        noFavoritesText.setVisibility(View.VISIBLE);
                        favoritesList.setVisibility(View.GONE);
                    } else {
                        noFavoritesText.setVisibility(View.GONE);
                        favoritesList.setVisibility(View.VISIBLE);
                    }
        });
        ViewModelProviders.of(this).get(FavoritesViewModel.class)
                .getFavoriteRecipes().observe(this, data -> recipes = data);
    }

}
