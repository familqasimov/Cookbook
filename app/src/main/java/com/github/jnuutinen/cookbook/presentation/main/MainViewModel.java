package com.github.jnuutinen.cookbook.presentation.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final LiveData<List<Category>> categories;
    private final LiveData<List<CombineDao.combinedRecipe>> favoriteCombinedRecipes;
    private final LiveData<List<Recipe>> favoriteRecipes;
    private final LiveData<List<Recipe>> recipes;
    private final LiveData<List<CombineDao.combinedRecipe>> combinedRecipes;
    private DataRepository dataRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        dataRepository = ((CookbookApp) application).getRepository();
        categories = dataRepository.getCategories();
        favoriteCombinedRecipes = dataRepository.getFavoriteCombinedRecipes();
        favoriteRecipes = dataRepository.getFavoriteRecipes();
        combinedRecipes = dataRepository.getCombinedRecipes();
        recipes = dataRepository.getRecipes();
    }

    void deleteCategory(Category category) {
        dataRepository.deleteCategory(category);
    }

    LiveData<List<Category>> getCategories() {
        return categories;
    }

    LiveData<List<CombineDao.combinedRecipe>> getCombinedRecipes() {
        return combinedRecipes;
    }

    void deleteAllCategories() {
        dataRepository.deleteAllCategories();
    }

    void deleteAllRecipes() {
        dataRepository.deleteAllRecipes();
    }

    LiveData<List<CombineDao.combinedRecipe>> getFavoriteCombinedRecipes() {
        return favoriteCombinedRecipes;
    }

    LiveData<List<Recipe>> getFavoriteRecipes() {
        return favoriteRecipes;
    }

    LiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    void updateCategory(Category category) {
        dataRepository.updateCategory(category);
    }
}
