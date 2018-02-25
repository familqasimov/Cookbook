package com.github.jnuutinen.cookbook.presentation.main;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.categories.CategoriesActivity;
import com.github.jnuutinen.cookbook.presentation.createrecipe.CreateRecipeActivity;
import com.github.jnuutinen.cookbook.presentation.viewrecipe.ViewRecipeActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ADD_RECIPE = 1;
    private static final int SORT_NAME = 0;
    private static final int SORT_CATEGORY = 1;
    private static int sort = SORT_NAME;

    @BindView(R.id.list_recipes) ListView recipeList;
    @BindView(R.id.text_sort) TextView sortText;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private List<Recipe> liveRecipes;
    private RecipeAdapter adapter;
    private AlertDialog sortDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        createSortDialog();
        observe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ADD_RECIPE:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(recipeList, R.string.alert_recipe_saved, Snackbar.LENGTH_LONG)
                            .setAction(R.string.alert_recipe_saved, null).show();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_sort) {
            sortDialog.show();
            return true;
        } else if (id == R.id.action_categories) {
            startActivity(new Intent(this, CategoriesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_add_recipe)
    void addRecipe() {
        startActivityForResult(new Intent(this, CreateRecipeActivity.class),
                REQUEST_ADD_RECIPE);
    }

    @OnItemClick(R.id.list_recipes)
    void viewRecipe(int position) {
        Intent intent = new Intent(this, ViewRecipeActivity.class);
        intent.putExtra("recipe", liveRecipes.get(position));
        startActivity(intent);
    }

    private void createSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_sort_recipes)
                .setItems(R.array.sort_types, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            sort = SORT_NAME;
                            break;
                        case 1:
                            sort = SORT_CATEGORY;
                            break;
                    }
                    observe();
                });
        sortDialog = builder.create();
    }

    private void observe() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, recipes -> {
            liveRecipes = sortRecipes(recipes);
            adapter = new RecipeAdapter(this, recipes);
                    recipeList.setAdapter(adapter);
                }
        );
    }

    private List<Recipe> sortRecipes(List<Recipe> toBeSorted) {
        // name order comparator
        Comparator<Recipe> nameOrder = (entry1, entry2) -> {
            final String name1 = entry1.getName();
            final String name2 = entry2.getName();
            return name1.compareToIgnoreCase(name2);
        };
        // category order comparator
        Comparator<Recipe> catOrder = (entry1, entry2) -> {
            final Integer cat1 = entry1.getCategoryId();
            final Integer cat2 = entry2.getCategoryId();
            if (cat1 == null && cat2 == null) {
                return 0;
            } else if (cat1 == null) {
                return -1;
            } else if (cat2 == null) {
                return 1;
            }
            return cat1.compareTo(cat2);
        };
        // Sort by name first
        Collections.sort(toBeSorted, nameOrder);
        if (sort == SORT_NAME) {
            sortText.setText(R.string.title_sorted_by_name);
        }
        if (sort == SORT_CATEGORY) {
            Collections.sort(toBeSorted, catOrder);
            sortText.setText(R.string.title_sorted_by_category);
        }
        return toBeSorted;
    }
}
