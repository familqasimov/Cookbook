package com.github.jnuutinen.cookbook.presentation.favorites;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class FavoritesViewModel extends AndroidViewModel {
    private final LiveData<List<Recipe>> recipes;
    private final LiveData<List<CombineDao.combinedRecipe>> combinedRecipes;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        recipes = ((CookbookApp) application).getRepository().getFavoriteRecipes();
        combinedRecipes = ((CookbookApp) application).getRepository().getFavoriteCombinedRecipes();
    }

    LiveData<List<CombineDao.combinedRecipe>> getFavoriteCombinedRecipes() {
        return combinedRecipes;
    }

    LiveData<List<Recipe>> getFavoriteRecipes() {
        return recipes;
    }
}
