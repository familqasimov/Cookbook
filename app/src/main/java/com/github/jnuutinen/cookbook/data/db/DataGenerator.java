package com.github.jnuutinen.cookbook.data.db;

import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class DataGenerator {
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

    static List<Recipe> generateRecipes() {
        List<Recipe> recipes = new ArrayList<>(RECIPE_NAME.length);
        for (int i = 0; i < RECIPE_NAME.length; i++) {
            Recipe recipe = new Recipe();
            recipe.setName(RECIPE_NAME[i]);
            recipe.setCategoryId(null);
            recipe.setInstructions(RECIPE_INSTRUCTIONS[i]);
            ArrayList<String> ingredients = new ArrayList<>(RECIPE_INGREDIENTS[0].length);
            ingredients.addAll(Arrays.asList(RECIPE_INGREDIENTS[i])
                    .subList(0, RECIPE_INGREDIENTS[0].length));
            recipe.setIngredients(ingredients);
            recipes.add(recipe);
        }
        return recipes;
    }

    static List<Category> generateCategories() {
        List<Category> categories = new ArrayList<>(CATEGORY_NAME.length);
        for (String aCATEGORY_NAME : CATEGORY_NAME) {
            categories.add(new Category(aCATEGORY_NAME));
        }
        return categories;
    }
}
