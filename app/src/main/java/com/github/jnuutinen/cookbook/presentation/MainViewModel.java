package com.github.jnuutinen.cookbook.presentation;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<Recipe>> observableRecipes;
    private final MediatorLiveData<List<Category>> observableCategories;


    public MainViewModel(@NonNull Application application) {
        super(application);
        observableRecipes = new MediatorLiveData<>();
        //observableRecipes.setValue(null);
        observableCategories = new MediatorLiveData<>();
        //observableCategories.setValue(null);
        LiveData<List<Recipe>> recipes = (((CookbookApp) application).getRepository().getRecipes());
        observableRecipes.addSource(recipes, observableRecipes::setValue);
        LiveData<List<Category>> categories = ((CookbookApp) application).getRepository().getCategories();
        observableCategories.addSource(categories, observableCategories::setValue);
    }

    public LiveData<List<Recipe>> getRecipes() {
        return observableRecipes;
    }

    public LiveData<List<Category>> getCategories() {
        return observableCategories;
    }
}
