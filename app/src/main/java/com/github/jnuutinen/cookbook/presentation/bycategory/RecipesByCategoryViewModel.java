package com.github.jnuutinen.cookbook.presentation.bycategory;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class RecipesByCategoryViewModel extends AndroidViewModel {
    private DataRepository dataRepository;

    public RecipesByCategoryViewModel(@NonNull Application application) {
        super(application);
        dataRepository = ((CookbookApp) application).getRepository();
    }

    LiveData<List<Recipe>> getLiveRecipesByCategory(Category category) {
        return dataRepository.getRecipes(category);
    }
}
