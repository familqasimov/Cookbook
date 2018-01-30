package com.github.jnuutinen.cookbook.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.github.jnuutinen.cookbook.data.db.entity.RecipeEntity;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM RecipeEntity")
    LiveData<List<RecipeEntity>> getAll();

    @Query("SELECT * FROM RecipeEntity WHERE id = :id LIMIT 1")
    LiveData<RecipeEntity> getById(int id);

    @Query("SELECT * FROM RecipeEntity WHERE name = :name")
    LiveData<List<RecipeEntity>> getAllByName(String name);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(RecipeEntity recipeEntity);

    @Insert
    void insert(RecipeEntity recipeEntity);

    @Insert
    void insertAll(List<RecipeEntity> recipeEntities);

    @Delete
    void delete(RecipeEntity recipeEntity);
}
