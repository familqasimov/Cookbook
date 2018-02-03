package com.github.jnuutinen.cookbook.presentation;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

public class CreateRecipeViewModel extends AndroidViewModel {
    private final CookbookApp application;

    public CreateRecipeViewModel(@NonNull Application application) {
        super(application);
        this.application = (CookbookApp) application;
    }

    public void insertRecipe(Recipe recipe) {
        application.getRepository().saveRecipe(recipe);
    }
}
