package com.github.jnuutinen.cookbook.unit;

import android.os.Parcel;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import com.github.jnuutinen.cookbook.data.db.entity.Category;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class CategoryEntityTest {
    private static final Integer TEST_ID = 3;
    private static final String TEST_NAME = "Category name";
    private Category category;

    @Before
    public void createRecipe() {
        category = new Category(TEST_ID, TEST_NAME);
    }

    @Test
    public void category_ParcelableWriteRead() {
        Parcel parcel = Parcel.obtain();
        category.writeToParcel(parcel, category.describeContents());
        parcel.setDataPosition(0);
        Category fromParcel = Category.CREATOR.createFromParcel(parcel);

        assertThat(fromParcel.getId(), is(TEST_ID));
        assertThat(fromParcel.getName(), is(TEST_NAME));
    }
}
