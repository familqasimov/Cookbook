package com.github.jnuutinen.cookbook.presentation.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Recipe>> observableRecipes;

    public MainViewModel(@NonNull Application application) {
        super(application);
        //observableRecipes = new MediatorLiveData<>();
        observableRecipes = (((CookbookApp) application).getRepository().getLiveRecipes());
    }

    LiveData<List<Recipe>> getRecipes() {
        return observableRecipes;
    }
}
