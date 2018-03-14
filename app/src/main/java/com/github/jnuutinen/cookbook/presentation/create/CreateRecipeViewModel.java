package com.github.jnuutinen.cookbook.presentation.createrecipe;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class CreateRecipeViewModel extends AndroidViewModel {
    private final LiveData<List<Category>> observableCategories;
    private final LiveData<List<Recipe>> observableRecipes;
    private DataRepository dataRepository;

    public CreateRecipeViewModel(@NonNull Application application) {
        super(application);
        dataRepository = ((CookbookApp) application).getRepository();
        observableCategories = dataRepository.getCategories();
        observableRecipes = dataRepository.getRecipes();
    }

    LiveData<List<Category>> getCategories() {
        return observableCategories;
    }

    LiveData<List<Recipe>> getRecipes() {
        return observableRecipes;
    }

    void insertRecipe(Recipe recipe) {
        dataRepository.insertRecipe(recipe);
    }
}
