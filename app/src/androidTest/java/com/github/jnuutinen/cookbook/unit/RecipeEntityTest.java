package com.github.jnuutinen.cookbook.unit;

import android.os.Parcel;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class RecipeEntityTest {
    private static final Integer TEST_ID = 3;
    private static final String TEST_NAME = "Recipe name";
    private static final Integer TEST_CATEGORY_ID = 7;
    private static final String TEST_INGREDIENT_1 = "Ingredient 1";
    private static final String TEST_INGREDIENT_2 = "Ingredient 2";
    private static final List<String> TEST_INGREDIENTS = Arrays.asList(TEST_INGREDIENT_1,
            TEST_INGREDIENT_2);
    private static final String TEST_INSTRUCTIONS = "Recipe instructions";
    private static final int TEST_IS_FAVORITE = 1;
    private Recipe recipe;

    @Before
    public void createRecipe() {
        recipe = new Recipe(TEST_ID, TEST_NAME, TEST_CATEGORY_ID, TEST_INGREDIENTS,
                TEST_INSTRUCTIONS, TEST_IS_FAVORITE);
    }

    @Test
    public void recipe_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        recipe.writeToParcel(parcel, recipe.describeContents());
        parcel.setDataPosition(0);
        Recipe fromParcel = Recipe.CREATOR.createFromParcel(parcel);

        assertThat(fromParcel.getId(), is(TEST_ID));
        assertThat(fromParcel.getName(), is(TEST_NAME));
        assertThat(fromParcel.getCategoryId(), is(TEST_CATEGORY_ID));
        assertThat(fromParcel.getIngredients().size(), is(2));
        assertThat(fromParcel.getIngredients().get(1), is(TEST_INGREDIENT_2));
        assertThat(fromParcel.getInstructions(), is(TEST_INSTRUCTIONS));
        assertThat(fromParcel.getIsFavorite(), is(TEST_IS_FAVORITE));
    }
}
