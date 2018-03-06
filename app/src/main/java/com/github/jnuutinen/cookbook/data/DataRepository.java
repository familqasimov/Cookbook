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
    private MediatorLiveData<List<Recipe>> recipes;
    private MediatorLiveData<List<Category>> categories;
    private MediatorLiveData<List<CombineDao.combinedRecipe>> combinedRecipes;

    private DataRepository(final AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        recipes = new MediatorLiveData<>();
        categories = new MediatorLiveData<>();
        combinedRecipes = new MediatorLiveData<>();
        recipes.addSource(appDatabase.getRecipes(),
                recipeEntities -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        recipes.postValue(recipeEntities);
                    }
                });
        categories.addSource(appDatabase.getCategories(),
                categoryEntities -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        categories.postValue(categoryEntities);
                    }
                });
        combinedRecipes.addSource(appDatabase.getCombinedRecipes(),
                combinedRecipes -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        this.combinedRecipes.postValue(combinedRecipes);
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

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public LiveData<List<Recipe>> getRecipes(Category category) {
        return instance.appDatabase.getRecipes(category);
    }

    public LiveData<List<CombineDao.combinedRecipe>> getCombinedRecipes() {
        return combinedRecipes;
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
