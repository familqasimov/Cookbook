package com.github.jnuutinen.cookbook.presentation.main;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.github.jnuutinen.cookbook.BuildConfig;
import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.RecipeAdapter;
import com.github.jnuutinen.cookbook.presentation.Utils;
import com.github.jnuutinen.cookbook.presentation.about.AboutActivity;
import com.github.jnuutinen.cookbook.presentation.categories.CategoriesActivity;
import com.github.jnuutinen.cookbook.presentation.createrecipe.CreateRecipeActivity;
import com.github.jnuutinen.cookbook.presentation.favorites.FavoritesActivity;
import com.github.jnuutinen.cookbook.presentation.viewrecipe.ViewRecipeActivity;
import com.rubengees.introduction.IntroductionActivity;
import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.Option;
import com.rubengees.introduction.Slide;

import java.util.ArrayList;
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
    public static final int REQUEST_VIEW_RECIPE = 2;
    public static final int REQUEST_CATEGORIES = 3;
    private static final int SORT_NAME = 0;
    private static final int SORT_CATEGORY = 1;
    private static final String STATE_SEARCH = "search";

    private static int sort = SORT_NAME;

    @BindView(R.id.edit_search_recipe) EditText searchEditText;
    @BindView(R.id.text_no_recipes) TextView noRecipesText;
    @BindView(R.id.list_recipes) ListView recipeList;
    @BindView(R.id.text_sort) TextView sortText;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private MainViewModel viewModel;
    private String search;
    private List<CombineDao.combinedRecipe> combinedRecipes;
    private List<Recipe> recipes;
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
        checkFirstRun();
        makeSearchListener();
        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                searchEditText.setVisibility(View.VISIBLE);
                searchEditText.requestFocus();
            } else {
                searchEditText.setText("");
                searchEditText.setVisibility(GONE);
            }
        });

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
                break;
            case IntroductionBuilder.INTRODUCTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    for (Option option : data.<Option>getParcelableArrayListExtra(IntroductionActivity.
                            OPTION_RESULT)) {
                        if (option.getPosition() == 0) {
                            if (!option.isActivated()) {
                                // Delete example recipes
                                viewModel.deleteAllRecipes();
                            }
                        } else if (option.getPosition() == 1) {
                            if (!option.isActivated()) {
                                // Delete example categories
                                viewModel.deleteAllCategories();
                            }
                        }
                    }
                } else {
                    // Introduction skipped
                    viewModel.deleteAllRecipes();
                    viewModel.deleteAllCategories();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                toggleSearch();
                break;
            case R.id.action_sort:
                sortDialog.show();
                break;
            case R.id.action_favorites:
                startActivity(new Intent(this, FavoritesActivity.class));
                break;
            case R.id.action_categories:
                startActivityForResult(new Intent(this, CategoriesActivity.class),
                        REQUEST_CATEGORIES);
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
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
        CombineDao.combinedRecipe combined = combinedRecipes.get(position);
        if (recipes != null) {
            Recipe foundRecipe = null;
            for (Recipe recipe : recipes) {
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

    private void checkFirstRun() {
        final String PREFS_NAME = "com.github.jnuutinen.cookbook";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {
            // Normal run
            return;
        } else if (savedVersionCode == DOESNT_EXIST) {
            new IntroductionBuilder(this).withSlides(generateSlides()).introduceMyself();
        }/* else if (currentVersionCode > savedVersionCode) {
            // Upgrade
        }*/

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
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

    private List<Slide> generateSlides() {
        List<Slide> result = new ArrayList<>();

        result.add(new Slide()
                .withTitle(R.string.title_introduction_example_recipes)
                .withColorResource(R.color.colorPrimary)
                .withOption(new Option(R.string.option_introduction_recipes, true))
        );

        result.add(new Slide()
                .withTitle(R.string.title_introduction_example_categories)
                .withColorResource(R.color.colorAccent)
                .withOption(new Option(R.string.option_introduction_categories, true))
        );

        return result;
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
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, data -> recipes = data);

        viewModel.getCombinedRecipes().observe(this, data -> {
            if (data == null || data.size() == 0) {
                noRecipesText.setVisibility(VISIBLE);
                sortText.setVisibility(GONE);
            } else {
                noRecipesText.setVisibility(GONE);
                sortText.setVisibility(VISIBLE);
            }
            data = Utils.sortByName(data);
            if (sort == SORT_CATEGORY) {
                data = Utils.sortByCategory(data);
                sortText.setText(R.string.title_sorted_by_category);
            } else {
                sortText.setText(R.string.title_sorted_by_name);
            }
            combinedRecipes = data;
            adapter = new RecipeAdapter(this, data);
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
}
