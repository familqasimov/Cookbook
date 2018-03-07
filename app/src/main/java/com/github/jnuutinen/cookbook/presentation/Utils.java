package com.github.jnuutinen.cookbook.presentation;

import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;

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
    public static List<CombineDao.combinedRecipe> sortByCategory(List<CombineDao.combinedRecipe> list) {
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
     * Sorts a combinedRecipe list by recipe name
     * @param list List to be sorted
     * @return The sorted list
     */
    public static List<CombineDao.combinedRecipe> sortByName(List<CombineDao.combinedRecipe> list) {
        Comparator<CombineDao.combinedRecipe> nameOrder = (entry1, entry2) -> {
            final String name1 = entry1.recipeName;
            final String name2 = entry2.recipeName;
            return name1.compareToIgnoreCase(name2);
        };
        // category order comparator
        // Sort by name first
        Collections.sort(list, nameOrder);
        return list;
    }
}
