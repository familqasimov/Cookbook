package com.github.jnuutinen.cookbook;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.data.db.entity.CategoryEntity;
import com.github.jnuutinen.cookbook.data.db.entity.RecipeEntity;

import java.util.List;


public class MainViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<RecipeEntity>> observableRecipes;
    private final MediatorLiveData<List<CategoryEntity>> observableCategories;
    private final App application;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = (App) application;
        observableRecipes = new MediatorLiveData<>();
        //observableRecipes.setValue(null);
        observableCategories = new MediatorLiveData<>();
        //observableCategories.setValue(null);
        LiveData<List<RecipeEntity>> recipes = ((this.application).getRepository().getRecipes());
        observableRecipes.addSource(recipes, observableRecipes::setValue);
        LiveData<List<CategoryEntity>> categories = (this.application).getRepository().getCategories();
        observableCategories.addSource(categories, observableCategories::setValue);
    }

    public LiveData<List<RecipeEntity>> getRecipes() {
        return observableRecipes;
    }

    public LiveData<List<CategoryEntity>> getCategories() {
        return observableCategories;
    }
}
