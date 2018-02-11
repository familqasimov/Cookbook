package com.github.jnuutinen.cookbook.data.db;

import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Creates example recipes.
 */
class DataGenerator {
    private static final String[] RECIPE_NAME = new String[] {
            "Macaroni and Cheese", "Best brownies", "Simple whole wheat bread"
    };
    private static final String[][] RECIPE_INGREDIENTS = new String[][] {
            // Recipe 1 ingredients
            {"1 (8 ounce) box elbow macaroni", "1/4 cup butter", "1/4 cup flour",
                    "1/2 teaspoon salt", "ground black pepper to taste", "2 cups milk",
                    "2 cups shredded Cheddar cheese"},

            // Recipe 2 ingredients
            {"1/2 cup butter", "3 tablespoons softened butter", "1 cup white sugar", "2 eggs",
                    "2 teaspoon vanilla extract", "1/3 cup + 3 tbs unsweetened cocoa powder",
                    "1/2 cup flour", "1/4 teaspoon salt", "1/4 teaspoon baking powder",
                    "1 tablespoon honey", "1 cup confectioners' sugar"},

            // Recipe 3 ingredients
            {"3 cups warm water", "2 (.25 ounce) packages dry yeast", "1/3 cup honey",
                    "5 cups bread flour", "3 tablespoons butter, melted", "1/3 cup honey",
                    "1 tablespoon salt", "3 1/2 cups whole wheat flour", "2 tablespoons butter, melted"}
    };

    private static final String[] RECIPE_INSTRUCTIONS = new String[] {
            // Recipe 1 instructions
            "Bring a large pot of lightly salted water to a boil. Cook elbow macaroni in the " +
                    "boiling water, stirring occasionally until cooked through but firm to the bite, " +
                    "8 minutes. Drain.\n\n" +
                    "Melt butter in a saucepan over medium heat; stir in flour, salt, and pepper until " +
                    "smooth, about 5 minutes. Slowly pour milk into butter-flour mixture while " +
                    "continuously stirring  until mixture is smooth and bubbling, about 5 minutes. " +
                    "Add cheddar cheese to milk mixture and stir until cheese is melted, 2 to 4 minutes.\n\n" +
                    "Fold macaroni into cheese sauce until coated.",

            // Recipe 2 instructions
            "Preheat oven to 350 degrees F (175 degrees C). Grease and flour an 8-inch square pan.\n\n" +
                    "In a large saucepan, melt 1/2 cup butter. Remove from heat, and stir in " +
                    "sugar, eggs, and 1 teaspoon vanilla. Beat in 1/3 cup cocoa, 1/2 cup flour, " +
                    "salt, and baking powder. Spread batter into prepared pan.\n\n" +
                    "Bake in preheated oven for 25 to 30 minutes. Do not overcook.\n\n" +
                    "To Make Frosting: Combine 3 tablespoons softened butter, 3 tablespoons " +
                    "cocoa, honey, 1 teaspoon vanilla extract, and 1 cup confectioners' sugar. " +
                    "Stir until smooth. Frost brownies while they are still warm.",

            // Recipe 3 instructions
            "In a large bowl, mix warm water, yeast, and 1/3 cup honey. Add 5 cups white bread " +
                    "flour, and stir to combine. Let set for 30 minutes, or until big and bubbly.\n\n" +
                    "Mix in 3 tablespoons melted butter, 1/3 cup honey, and salt. Stir in 2 cups " +
                    "whole wheat flour. Flour a flat surface and knead with whole wheat flour " +
                    "until not real sticky - just pulling away from the counter, but still " +
                    "sticky to touch. This may take an additional 2 to 4 cups of whole wheat " +
                    "flour. Place in a greased bowl, turning once to coat the surface of the " +
                    "dough. Cover with a dishtowel. Let rise in a warm place until doubled.\n\n" +
                    "Punch down, and divide into 3 loaves. Place in greased 9 x 5 inch loaf " +
                    "pans, and allow to rise until dough has topped the pans by one inch.\n\n" +
                    "Bake at 350 degrees F (175 degrees C) for 25 to 30 minutes; do not overbake. " +
                    "Lightly brush the tops of loaves with 2 tablespoons melted butter or " +
                    "margarine when done to prevent crust from getting hard. Cool completely."
    };

    private static final String[] CATEGORY_NAME = new String[] {
            // Recipe 1 category
            "Pastas",

            // Recipe 2 category
            "Desserts",

            // Recipe 3 category
            "Breads"
    };

    static List<Category> generateCategories() {
        List<Category> categories = new ArrayList<>(CATEGORY_NAME.length);
        for (String aCATEGORY_NAME : CATEGORY_NAME) {
            categories.add(new Category(aCATEGORY_NAME));
        }
        return categories;
    }

    static List<Recipe> generateRecipes() {
        List<Recipe> recipes = new ArrayList<>(RECIPE_NAME.length);
        for (int i = 0; i < RECIPE_NAME.length; i++) {
            Recipe recipe = new Recipe();
            recipe.setName(RECIPE_NAME[i]);
            recipe.setCategory(CATEGORY_NAME[i]);
            recipe.setInstructions(RECIPE_INSTRUCTIONS[i]);
            ArrayList<String> ingredients = new ArrayList<>(RECIPE_INGREDIENTS[0].length);
            ingredients.addAll(Arrays.asList(RECIPE_INGREDIENTS[i])
                    .subList(0, RECIPE_INGREDIENTS[0].length));
            recipe.setIngredients(ingredients);
            recipes.add(recipe);
        }
        return recipes;
    }
}
