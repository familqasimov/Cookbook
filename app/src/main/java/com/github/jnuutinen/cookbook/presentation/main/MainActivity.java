package com.github.jnuutinen.cookbook.presentation.main;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
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

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ADD_RECIPE = 1;
    private static final int REQUEST_VIEW_RECIPE = 2;
    private static final int REQUEST_CATEGORIES = 3;
    private static final int SORT_NAME = 0;
    private static final int SORT_CATEGORY = 1;
    private static final String STATE_SEARCH = "search";
    private static int sort = SORT_NAME;

    @BindView(R.id.edit_search_recipe) EditText searchEditText;
    @BindView(R.id.text_no_recipes) TextView noRecipesText;
    @BindView(R.id.list_recipes) ListView recipeList;
    @BindView(R.id.text_sort) TextView sortText;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private String search;
    private List<CombineDao.combinedRecipe> liveCombinedRecipes;
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

        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.requestFocus();
            } else {
                searchEditText.setText("");
                searchEditText.setVisibility(GONE);
            }
        });
        makeSearchListener();

        if (savedInstanceState != null) {
            search = savedInstanceState.getString(STATE_SEARCH);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (searchEditText.getVisibility() == VISIBLE) {
            savedInstanceState.putString(STATE_SEARCH, searchEditText.getText().toString());
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ADD_RECIPE:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(recipeList, R.string.alert_recipe_saved, Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            case REQUEST_VIEW_RECIPE:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(recipeList, R.string.alert_recipe_deleted, Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            case REQUEST_CATEGORIES:
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "REQUEST_CATEGORIES result: RESULT_OK");
                    String filterExtra = data.getStringExtra("filter");
                    if (filterExtra != null) {
                        Log.d(TAG, "filter: " + filterExtra);
                        if (searchEditText.getVisibility() == VISIBLE) {
                            searchEditText.setText(filterExtra);
                        } else {
                            toggleSearch(filterExtra);
                        }
                    } else {
                        Log.d(TAG, "filterExtra == null");
                    }
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                toggleSearch();
                break;
            case R.id.action_settings:
                break;
            case R.id.action_sort:
                sortDialog.show();
                break;
            case R.id.action_categories:
                startActivityForResult(new Intent(this, CategoriesActivity.class),
                        REQUEST_CATEGORIES);
                break;
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
        CombineDao.combinedRecipe combined = liveCombinedRecipes.get(position);
        if (liveRecipes != null) {
            Recipe foundRecipe = null;
            for (Recipe recipe : liveRecipes) {
                if (recipe.getName().equals(combined.recipeName)) {
                    foundRecipe = recipe;
                    break;
                }
            }
            if (foundRecipe != null) {
                intent.putExtra("recipe", foundRecipe);
                startActivityForResult(intent, REQUEST_VIEW_RECIPE);
            }
        }
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

    private void makeSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

    private void observe() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, recipes -> liveRecipes = recipes);

        viewModel.getCombinedRecipes().observe(this, combinedRecipes -> {
            if (combinedRecipes != null) {
                if (combinedRecipes.size() > 0) {
                    noRecipesText.setVisibility(GONE);
                    sortText.setVisibility(View.VISIBLE);
                } else {
                    noRecipesText.setVisibility(View.VISIBLE);
                    sortText.setVisibility(GONE);
                }
            } else {
                noRecipesText.setVisibility(View.VISIBLE);
                sortText.setVisibility(GONE);
            }
            liveCombinedRecipes = sortRecipes(combinedRecipes);
            adapter = new RecipeAdapter(this, combinedRecipes);
            recipeList.setAdapter(adapter);
            if (searchEditText.getVisibility() == VISIBLE) {
                adapter.getFilter().filter(searchEditText.getText().toString());
            } else if (search != null) {
                searchEditText.setVisibility(VISIBLE);
                searchEditText.setText(search);
                adapter.getFilter().filter(search);
                search = null;
            }
        });
    }

    private void toggleSearch() {
        if (searchEditText.getVisibility() == View.VISIBLE) {
            searchEditText.setText("");
            searchEditText.setVisibility(View.GONE);
        } else {
            searchEditText.setVisibility(View.VISIBLE);
            searchEditText.requestFocus();
        }
    }

    private void toggleSearch(String searchString) {
        if (searchEditText.getVisibility() == View.VISIBLE) {
            searchEditText.setText("");
            searchEditText.setVisibility(View.GONE);
        } else {
            searchEditText.setVisibility(View.VISIBLE);
            searchEditText.requestFocus();
            searchEditText.setText(searchString);
        }
    }

    private List<CombineDao.combinedRecipe> sortRecipes(List<CombineDao.combinedRecipe> toBeSorted) {
        // name order comparator
        Comparator<CombineDao.combinedRecipe> nameOrder = (entry1, entry2) -> {
            final String name1 = entry1.recipeName;
            final String name2 = entry2.recipeName;
            return name1.compareToIgnoreCase(name2);
        };
        // category order comparator
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
