package com.github.jnuutinen.cookbook.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.github.jnuutinen.cookbook.data.db.AppDatabase;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class DataRepository {
    private static DataRepository instance;

    private final AppDatabase appDatabase;
    private MediatorLiveData<List<Recipe>> observableRecipes;
    private MediatorLiveData<List<Category>> observableCategories;

    private DataRepository(final AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        observableRecipes = new MediatorLiveData<>();
        observableCategories = new MediatorLiveData<>();
        observableRecipes.addSource(appDatabase.getAllRecipes(),
                recipeEntities -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        observableRecipes.postValue(recipeEntities);
                    }
                });
        observableCategories.addSource(appDatabase.getAllCategories(),
                categoryEntities -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        observableCategories.postValue(categoryEntities);
                    }
                });
    }

    public static DataRepository getInstance(final AppDatabase appDatabase) {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(appDatabase);
                }
            }
        }
        return instance;
    }

    public void deleteRecipe(Recipe recipe) {
        instance.appDatabase.deleteRecipe(recipe);
    }

    @SuppressWarnings("unused")
    public LiveData<List<Category>> getCategories() {
        return observableCategories;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return observableRecipes;
    }

    public void saveRecipe(Recipe recipe) {
        instance.appDatabase.insertRecipe(recipe);
    }
}
