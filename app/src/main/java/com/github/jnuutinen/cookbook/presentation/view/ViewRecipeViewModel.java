package com.github.jnuutinen.cookbook.presentation.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class ViewRecipeViewModel extends AndroidViewModel {
    private DataRepository dataRepository;
    private final LiveData<List<CombineDao.combinedRecipe>> combinedRecipes;
    private Recipe recipe;

    public ViewRecipeViewModel(@NonNull Application application) {
        super(application);
        dataRepository = ((CookbookApp) application).getRepository();
        combinedRecipes = dataRepository.getCombinedRecipes();
    }

    void cacheRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    void deleteRecipe(Recipe recipe) {
        dataRepository.deleteRecipe(recipe);
    }

    Recipe getCachedRecipe() {
        return recipe;
    }

    LiveData<List<CombineDao.combinedRecipe>> getCombinedRecipes() {
        return combinedRecipes;
    }

    void updateRecipe(Recipe recipe) {
        dataRepository.updateRecipe(recipe);
    }
}
