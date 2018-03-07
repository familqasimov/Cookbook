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
    private MediatorLiveData<List<Category>> categories;
    private MediatorLiveData<List<CombineDao.combinedRecipe>> combinedRecipes;
    private MediatorLiveData<List<CombineDao.combinedRecipe>> favoriteCombinedRecipes;
    private MediatorLiveData<List<Recipe>> favoriteRecipes;
    private MediatorLiveData<List<Recipe>> recipes;

    private DataRepository(final AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
        categories = new MediatorLiveData<>();
        combinedRecipes = new MediatorLiveData<>();
        favoriteCombinedRecipes = new MediatorLiveData<>();
        favoriteRecipes = new MediatorLiveData<>();
        recipes = new MediatorLiveData<>();
        categories.addSource(appDatabase.getCategories(),
                data -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        categories.postValue(data);
                    }
                });
        combinedRecipes.addSource(appDatabase.getCombinedRecipes(),
                data -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        combinedRecipes.postValue(data);
                    }
                });
        favoriteCombinedRecipes.addSource(appDatabase.getFavoriteCombinedRecipes(),
                data -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        favoriteCombinedRecipes.postValue(data);
                    }
                });
        favoriteRecipes.addSource(appDatabase.getFavoriteRecipes(),
                data -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        favoriteRecipes.postValue(data);
                    }
                });
        recipes.addSource(appDatabase.getRecipes(),
                data -> {
                    if (appDatabase.getDatabaseCreated().getValue() != null) {
                        recipes.postValue(data);
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

    public LiveData<List<CombineDao.combinedRecipe>> getCombinedRecipes() {
        return combinedRecipes;
    }

    public LiveData<List<CombineDao.combinedRecipe>> getFavoriteCombinedRecipes() {
        return favoriteCombinedRecipes;
    }

    public LiveData<List<Recipe>> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public LiveData<List<Recipe>> getRecipes(Category category) {
        return instance.appDatabase.getRecipes(category);
    }

    public void insertCategory(Category category) {
        instance.appDatabase.insertCategory(category);
    }

    public void insertRecipe(Recipe recipe) {
        instance.appDatabase.insertRecipe(recipe);
    }

    public void updateCategory(Category category) {
        instance.appDatabase.updateCategory(category);
    }

    public void updateRecipe(Recipe recipe) {
        instance.appDatabase.updateRecipe(recipe);
    }
}
