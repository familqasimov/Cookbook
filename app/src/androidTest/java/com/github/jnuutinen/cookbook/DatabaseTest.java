package com.github.jnuutinen.cookbook;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.github.jnuutinen.cookbook.data.db.AppDatabase;
import com.github.jnuutinen.cookbook.data.db.dao.CategoryDao;
import com.github.jnuutinen.cookbook.data.db.dao.RecipeDao;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private AppDatabase database;
    private CategoryDao categoryDao;
    private RecipeDao recipeDao;

    @Before
    public void createDatabase() {
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        categoryDao = database.categoryDao();
        recipeDao = database.recipeDao();
        List<Category> testCategories = new ArrayList<>(5);
        for (int i = 1; i <= 5; i++) {
            testCategories.add(new Category("category_" + i));
        }
        categoryDao.insertAll(testCategories);
        testCategories = categoryDao.getAll();
        List<Recipe> testRecipes = new ArrayList<>(5);
        for (int i = 1; i <= 5; i++) {
            ArrayList<String> ingredients = new ArrayList<>(2);
            ingredients.add("ingredient_1");
            ingredients.add("ingredient_2");
            testRecipes.add(new Recipe("recipe_" + i, testCategories.get(i-1).getId(),
                    ingredients, "instructions_recipe_" + i));
        }
        recipeDao.insertAll(testRecipes);
    }

    @After
    public void closeDatabase() throws IOException {
        database.close();
    }

    @Test
    public void readData() throws Exception {
        List<Recipe> recipes = database.getAllRecipes();
        List<Category> categories = database.getAllCategories();

        assertNotNull(recipes);
        assertNotNull(categories);

        // There should be 5 recipes and 5 categories
        assertEquals(5, recipes.size());
        assertEquals(5, categories.size());

        // Get category by name
        Category category = categoryDao.getByName("category_3");
        assertNotNull(category);
        assertEquals("category_3", category.getName());
        category = categoryDao.getByName("category_5");
        assertNotNull(category);
        assertEquals("category_5", category.getName());

        // Get recipe by name
        Recipe recipe = recipeDao.getByName("recipe_2");
        assertNotNull(recipe);
        assertEquals("recipe_2", recipe.getName());
        recipe = recipeDao.getByName("recipe_4");
        assertNotNull(recipe);
        assertEquals("recipe_4", recipe.getName());
    }

    @Test
    public void deleteData() throws Exception {
        // Delete recipe
        recipeDao.delete(recipeDao.getByName("recipe_2"));
        assertNull(recipeDao.getByName("recipe_2"));
        assertEquals(4, recipeDao.getAll().size());

        // Check recipe_3's category before deleting it
        assertEquals("category_3", categoryDao.getById(recipeDao.getByName("recipe_3")
                .getCategoryId()).getName());

        // Delete category
        categoryDao.delete(categoryDao.getByName("category_3"));
        assertNull(categoryDao.getByName("category_3"));
        assertEquals(4, categoryDao.getAll().size());

        // Affected recipes' category id should be 0
        assertNotNull(recipeDao.getByName("recipe_3"));
        assertEquals((Integer) 0, recipeDao.getByName("recipe_3").getCategoryId());
    }

    @Test
    public void updateData() throws Exception {
        // Update recipe
        Recipe recipe = recipeDao.getByName("recipe_1");
        recipe.setName("updated");
        recipeDao.update(recipe);
        assertNull(recipeDao.getByName("recipe_1"));
        assertEquals("category_1", categoryDao.getById(recipeDao.getByName("updated")
                .getCategoryId()).getName());

        // Update category
        Category category = categoryDao.getByName("category_1");
        Integer id = category.getId();
        category.setName("updated_category");
        categoryDao.update(category);
        assertEquals(id, categoryDao.getByName("updated_category").getId());
        assertEquals("updated_category", categoryDao.getById(id).getName());
        assertEquals(id, recipeDao.getByName("updated").getCategoryId());
    }
}
