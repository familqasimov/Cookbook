package com.github.jnuutinen.cookbook.data.db;


import com.github.jnuutinen.cookbook.data.db.entity.CategoryEntity;
import com.github.jnuutinen.cookbook.data.db.entity.RecipeEntity;

import java.util.ArrayList;
import java.util.List;

public class DataGenerator {
    private static final String[] RECIPE_NAME = new String[] {
            "Recipe1", "Recipe2", "Recipe3", "Recipe4"};
    private static final String[][] RECIPE_INGREDIENTS = new String[][] {
            {"Recipe1 ingredient1", "Recipe1 ingredient2", "Recipe1 ingredient3"},
            {"Recipe2 ingredient1", "Recipe2 ingredient2", "Recipe2 ingredient3"},
            {"Recipe3 ingredient1", "Recipe3 ingredient2", "Recipe3 ingredient3"},
            {"Recipe4 ingredient1", "Recipe4 ingredient2", "Recipe4 ingredient3"}};
    private static final String[] RECIPE_INSTRUCTIONS = new String[] {
            "Recipe1 instructions", "Recipe2 instructions",
            "Recipe3 instructions", "Recipe4 instructions"};
    private static final String[] CATEGORY_NAME = new String[] {
            "Category1", "Category2", "Category3", "Category4"};

    public static List<RecipeEntity> generateRecipes() {
        List<RecipeEntity> recipes = new ArrayList<>(RECIPE_NAME.length);
        for (int i = 0; i < RECIPE_NAME.length; i++) {
            RecipeEntity recipe = new RecipeEntity();
            recipe.setName(RECIPE_NAME[i]);
            recipe.setCategoryId(null);
            recipe.setInstructions(RECIPE_INSTRUCTIONS[i]);
            ArrayList<String> ingredients = new ArrayList<>(RECIPE_INGREDIENTS[0].length);
            for (int j = 0; j < RECIPE_INGREDIENTS[0].length; j++) {
                ingredients.add(RECIPE_INGREDIENTS[i][j]);
            }
            recipe.setIngredients(ingredients);
            recipes.add(recipe);
        }
        return recipes;
    }

    public static List<CategoryEntity> generateCategories() {
        List<CategoryEntity> categories = new ArrayList<>(CATEGORY_NAME.length);
        for (int i = 0; i < CATEGORY_NAME.length; i++) {
            categories.add(new CategoryEntity(CATEGORY_NAME[i]));
        }
        return categories;
    }
}
