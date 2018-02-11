package com.github.jnuutinen.cookbook.presentation.view;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

public class ViewRecipeViewModel extends AndroidViewModel {
    private DataRepository dataRepository;

    public ViewRecipeViewModel(@NonNull Application application) {
        super(application);
        dataRepository = ((CookbookApp) application).getRepository();
    }

    void deleteRecipe(Recipe recipe) {
        dataRepository.deleteRecipe(recipe);
    }
}
