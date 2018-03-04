package com.github.jnuutinen.cookbook.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.github.jnuutinen.cookbook.data.db.AppDatabase;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class DataRepository {
    private static DataRepository instance;

    private final AppDatabase appDatabase;
    private MediatorLiveData<List<Recipe>> observableRecipes;
    private MediatorLiveData<List<Category>> observableCategories;
    private MediatorLiveData<List<CombineDao.combinedRecipe>> observableCombinedRecipes;

    private DataRepository(final AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        observableRecipes = new MediatorLiveData<>();
        observableCategories = new MediatorLiveData<>();
        observableCombinedRecipes = new MediatorLiveData<>();
        observableRecipes.addSource(appDatabase.getAllLiveRecipes(),
                recipeEntities -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        observableRecipes.postValue(recipeEntities);
                    }
                });
        observableCategories.addSource(appDatabase.getAllLiveCategories(),
                categoryEntities -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        observableCategories.postValue(categoryEntities);
                    }
                });
        observableCombinedRecipes.addSource(appDatabase.getCombinedRecipes(),
                combinedRecipes -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        observableCombinedRecipes.postValue(combinedRecipes);
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

    public void deleteAllCategories() {
        instance.appDatabase.deleteAllCategories();
    }

    public void deleteAllRecipes() {
        instance.appDatabase.deleteAllRecipes();
    }

    public void deleteCategory(Category category) {
        instance.appDatabase.deleteCategory(category);
    }

    public void deleteRecipe(Recipe recipe) {
        instance.appDatabase.deleteRecipe(recipe);
    }

    public LiveData<List<Category>> getLiveCategories() {
        return observableCategories;
    }

    public LiveData<List<Recipe>> getLiveRecipes() {
        return observableRecipes;
    }

    public LiveData<List<CombineDao.combinedRecipe>> getLiveCombinedRecipes() {
        return observableCombinedRecipes;
    }

    public void insertRecipe(Recipe recipe) {
        instance.appDatabase.insertRecipe(recipe);
    }

    public void insertCategory(Category category) {
        instance.appDatabase.insertCategory(category);
    }

    public void updateCategory(Category category) {
        instance.appDatabase.updateCategory(category);
    }

    public void updateRecipe(Recipe recipe) {
        instance.appDatabase.updateRecipe(recipe);
    }
}
