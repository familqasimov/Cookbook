package com.github.jnuutinen.cookbook.presentation.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Recipe>> observableRecipes;
    private final LiveData<List<CombineDao.combinedRecipe>> observableCombinedRecipes;
    private DataRepository dataRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        dataRepository = ((CookbookApp) application).getRepository();
        observableRecipes = dataRepository.getLiveRecipes();
        observableCombinedRecipes = dataRepository.getLiveCombinedRecipes();
    }

    LiveData<List<CombineDao.combinedRecipe>> getCombinedRecipes() {
        return observableCombinedRecipes;
    }

    void deleteAllCategories() {
        dataRepository.deleteAllCategories();
    }

    void deleteAllRecipes() {
        dataRepository.deleteAllRecipes();
    }

    LiveData<List<Recipe>> getRecipes() {
        return observableRecipes;
    }
}
