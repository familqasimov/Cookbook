package com.github.jnuutinen.cookbook.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.github.jnuutinen.cookbook.data.db.entity.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM Category")
    LiveData<List<Category>> getAll();

    @Delete
    void delete(Category category);

    @Query("DELETE FROM Category")
    void deleteAll();

    @Update
    void update(Category category);

    @Insert
    void insert(Category category);

    @Insert
    void insertAll(List<Category> categoryEntities);
}
