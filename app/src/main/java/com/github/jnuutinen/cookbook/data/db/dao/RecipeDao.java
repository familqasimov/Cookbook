package com.github.jnuutinen.cookbook.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM Recipe")
    LiveData<List<Recipe>> getAll();

    @Query("SELECT * FROM Recipe WHERE category_id = :categoryId")
    LiveData<List<Recipe>> getByCategory(Integer categoryId);

    @Query("SELECT * FROM Recipe WHERE is_favorite = 1")
    LiveData<List<Recipe>> getFavorites();

    @Insert
    void insert(Recipe recipe);

    @Insert
    void insertAll(List<Recipe> recipeEntities);

    @Update
    void update(Recipe recipe);

    @Delete
    void delete(Recipe recipe);

    @Query("DELETE FROM Recipe")
    void deleteAll();
}
