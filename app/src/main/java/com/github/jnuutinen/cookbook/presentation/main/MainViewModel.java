package com.github.jnuutinen.cookbook.presentation.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

class MainViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<Recipe>> observableRecipes;

    MainViewModel(@NonNull Application application) {
        super(application);
        observableRecipes = new MediatorLiveData<>();
        LiveData<List<Recipe>> recipes = (((CookbookApp) application).getRepository().getRecipes());
        observableRecipes.addSource(recipes, observableRecipes::setValue);
    }

    LiveData<List<Recipe>> getRecipes() {
        return observableRecipes;
    }
}
