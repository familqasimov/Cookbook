package com.github.jnuutinen.cookbook.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM Recipe")
    LiveData<List<Recipe>> getAll();

    @Query("SELECT * FROM Recipe WHERE id = :id LIMIT 1")
    LiveData<Recipe> getById(int id);

    @Query("SELECT * FROM Recipe WHERE name = :name")
    LiveData<List<Recipe>> getAllByName(String name);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Recipe recipe);

    @Insert
    void insert(Recipe recipe);

    @Insert
    void insertAll(List<Recipe> recipeEntities);

    @Delete
    void delete(Recipe recipe);
}
