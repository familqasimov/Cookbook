package com.github.jnuutinen.cookbook.data.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.support.annotation.Nullable;

import java.util.List;

@Dao
public interface CombineDao {

    @Query("SELECT recipe.name AS recipeName, category.name AS categoryName FROM recipe "
            + "LEFT OUTER JOIN category ON recipe.category_id = category.id")
    LiveData<List<combinedRecipe>> getCombinedRecipes();

    @Query("SELECT recipe.name AS recipeName, category.name AS categoryName FROM recipe "
            + "LEFT OUTER JOIN category ON recipe.category_id = category.id "
            + "WHERE recipe.is_favorite = 1")
    LiveData<List<combinedRecipe>> getFavoriteCombinedRecipes();

    class combinedRecipe {
        public String recipeName;
        @Nullable
        public String categoryName;
    }
}
