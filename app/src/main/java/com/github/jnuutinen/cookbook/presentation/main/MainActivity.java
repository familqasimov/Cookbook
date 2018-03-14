package com.github.jnuutinen.cookbook.presentation.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.github.jnuutinen.cookbook.BuildConfig;
import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.about.AboutActivity;
import com.github.jnuutinen.cookbook.presentation.create.CreateRecipeActivity;
import com.github.jnuutinen.cookbook.presentation.view.ViewRecipeActivity;
import com.rubengees.introduction.IntroductionActivity;
import com.rubengees.introduction.IntroductionBuilder;
import com.rubengees.introduction.Option;
import com.rubengees.introduction.Slide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements
        AllRecipesFragment.OnRecipeSelectedListener,
        CategoriesFragment.CategoryFragmentListener {
    //private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ADD_RECIPE = 1;
    public static final int REQUEST_VIEW_RECIPE = 2;

    @BindView(R.id.container) ViewPager mViewPager;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private MainViewModel viewModel;
    private List<Category> categories;
    private List<Recipe> recipes;
    private Category editedCategory;
    private AlertDialog deleteCategoryDialog;
    private AlertDialog editCategoryDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        observe();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        buildDeleteCategoryDialog();
        buildEditCategoryDialog();
        checkFirstRun();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO: search filter
                return false;
            }
        });

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ADD_RECIPE:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(toolbar, R.string.alert_recipe_saved, Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
            case REQUEST_VIEW_RECIPE:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(toolbar, R.string.alert_recipe_deleted, Snackbar.LENGTH_LONG)
                            .show();
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
                    // Introduction skipped?
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
                // TODO: search widget
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCategoryDelete(Category category) {
        editedCategory = category;
        deleteCategoryDialog.setTitle(getString(R.string.title_deleting_category,
                editedCategory.getName()));
        deleteCategoryDialog.show();
    }

    @SuppressLint("InflateParams")
    @Override
    public void onCategoryEdit(Category category) {
        editedCategory = category;
        editCategoryDialog.setTitle(getString(R.string.title_editing_category,
                category.getName()));
        editCategoryDialog.show();
        ((EditText) getLayoutInflater().inflate(R.layout.dialog_edit_category, null)
                .findViewById(R.id.edit_category_name)).setText(category.getName());
    }

    @Override
    public void onRecipeSelected(Recipe recipe) {
        Intent intent = new Intent(this, ViewRecipeActivity.class);
        intent.putExtra("recipe", recipe);
        startActivityForResult(intent, REQUEST_VIEW_RECIPE);
    }

    @Override
    public void onRecipeSelected(String name) {
        Recipe recipe = null;
        for (Recipe r : recipes) {
            if (r.getName().equals(name)) {
                recipe = r;
                break;
            }
        }
        if (recipe != null) {
            Intent intent = new Intent(this, ViewRecipeActivity.class);
            intent.putExtra("recipe", recipe);
            startActivityForResult(intent, REQUEST_VIEW_RECIPE);
        }
    }

    @OnClick(R.id.button_add_recipe)
    void addRecipe() {
        startActivityForResult(new Intent(this, CreateRecipeActivity.class),
                REQUEST_ADD_RECIPE);
    }

    private void buildDeleteCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_delete_category)
                .setPositiveButton(R.string.yes, (dialog, which) ->
                        viewModel.deleteCategory(editedCategory))
                .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Do nothing
                });
        deleteCategoryDialog = builder.create();

    }

    @SuppressLint("InflateParams")
    private void buildEditCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_category, null);
        editCategoryDialog = builder.setView(dialogView)
                .setPositiveButton(R.string.action_save, (dialog, which) -> {
                    String categoryName = ((EditText) dialogView
                            .findViewById(R.id.edit_category_name)).getText().toString().trim();
                    if (categoryName.length() == 0) {
                        Snackbar.make(toolbar, R.string.alert_blank_category_name,
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        // Check for duplicate category
                        boolean duplicateFound = false;
                        for (Category c : categories) {
                            if (c.getName().toLowerCase().equals(categoryName.toLowerCase())
                                    && c != editedCategory) {
                                duplicateFound = true;
                                Snackbar.make(toolbar, R.string.category_name_duplicate,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            }
                        }
                        if (!duplicateFound) {
                            editedCategory.setName(categoryName);
                            viewModel.updateCategory(editedCategory);
                            Snackbar.make(toolbar, R.string.alert_category_saved,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                    ((EditText) dialogView.findViewById(R.id.edit_category_name)).setText("");
                }).setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Canceled, do nothing
                }).create();
    }

    private void checkFirstRun() {
        final String PREFS_NAME = "com.github.jnuutinen.cookbook";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOES_NOT_EXIST = -1;

        int currentVersionCode = BuildConfig.VERSION_CODE;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOES_NOT_EXIST);

        if (currentVersionCode == savedVersionCode) {
            return;
        } else if (savedVersionCode == DOES_NOT_EXIST) {
            new IntroductionBuilder(this).withSlides(generateSlides()).introduceMyself();
        }/* else if (currentVersionCode > savedVersionCode) {
            // Upgrade
        }*/

        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
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

    private void observe() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getCategories().observe(this, data -> categories = data);
        viewModel.getRecipes().observe(this, data -> recipes = data);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                default:
                    return AllRecipesFragment.newInstance();
                case 1:
                    return FavoriteRecipesFragment.newInstance();
                case 2:
                    return CategoriesFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
