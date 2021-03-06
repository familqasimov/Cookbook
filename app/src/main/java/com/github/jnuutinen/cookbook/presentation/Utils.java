package com.github.jnuutinen.cookbook.presentation;

import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for sorting lists with custom objects
 */
public class Utils {

    /**
     * Sorts a combinedRecipe list by category
     * @param list List to be sorted
     * @return The sorted list
     */
    public static List<CombineDao.combinedRecipe> sortCombinedRecipesByCategory(List<CombineDao.combinedRecipe> list) {
        Comparator<CombineDao.combinedRecipe> catOrder = (entry1, entry2) -> {
            final String cat1 = entry1.categoryName;
            final String cat2 = entry2.categoryName;
            if (cat1 == null && cat2 == null) {
                return 0;
            } else if (cat1 == null) {
                return 1;
            } else if (cat2 == null) {
                return -1;
            }
            return cat1.compareTo(cat2);
        };
        Collections.sort(list, catOrder);
        return list;
    }

    /**
     * Sorts a Category list
     * @param list List to be sorted
     * @return The sorted list
     */
    public static List<Category> sortCategoriesByName(List<Category> list) {
        Comparator<Category> nameOrder = (entry1, entry2) -> {
            final String name1 = entry1.getName();
            final String name2 = entry2.getName();
            return name1.compareToIgnoreCase(name2);
        };
        Collections.sort(list, nameOrder);
        return list;
    }

    /**
     * Sorts a combinedRecipe list by recipe name
     * @param list List to be sorted
     * @return The sorted list
     */
    public static List<CombineDao.combinedRecipe> sortCombinedRecipesByName(List<CombineDao.combinedRecipe> list) {
        Comparator<CombineDao.combinedRecipe> nameOrder = (entry1, entry2) -> {
            final String name1 = entry1.recipeName;
            final String name2 = entry2.recipeName;
            return name1.compareToIgnoreCase(name2);
        };
        Collections.sort(list, nameOrder);
        return list;
    }

    /**
     * Sorts a Recipe list by recipe name
     * @param list List to be sorted
     * @return The sorted list
     */
    public static List<Recipe> sortRecipesByName(List<Recipe> list) {
        Comparator<Recipe> nameOrder = (entry1, entry2) -> {
            final String name1 = entry1.getName();
            final String name2 = entry2.getName();
            return name1.compareToIgnoreCase(name2);
        };
        Collections.sort(list, nameOrder);
        return list;

    }
}
