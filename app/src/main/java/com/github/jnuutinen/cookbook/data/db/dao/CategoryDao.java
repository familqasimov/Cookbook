package com.github.jnuutinen.cookbook.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.github.jnuutinen.cookbook.data.db.entity.CategoryEntity;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM CategoryEntity")
    LiveData<List<CategoryEntity>> getAll();

    @Query("SELECT * FROM CategoryEntity WHERE id = :id LIMIT 1")
    LiveData<CategoryEntity> getById(int id);

    @Query("SELECT * FROM CategoryEntity WHERE name = :name LIMIT 1")
    LiveData<CategoryEntity> getByName(String name);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(CategoryEntity categoryEntity);

    @Insert
    void insert(CategoryEntity categoryEntity);

    @Insert
    void insertAll(List<CategoryEntity> categoryEntities);

    @Delete
    void delete(CategoryEntity categoryEntity);
}
