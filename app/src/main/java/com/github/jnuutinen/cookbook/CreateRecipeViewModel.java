package com.github.jnuutinen.cookbook;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.data.db.entity.RecipeEntity;

public class CreateRecipeViewModel extends AndroidViewModel {
    private final App application;

    public CreateRecipeViewModel(@NonNull Application application) {
        super(application);
        this.application = (App) application;
    }

    public void insertRecipe(RecipeEntity recipe) {
        application.getRepository().saveRecipe(recipe);
    }
}
