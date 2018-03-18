package com.github.jnuutinen.cookbook.presentation.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.BuildConfig;
import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.about.AboutActivity;
import com.github.jnuutinen.cookbook.presentation.create.CreateRecipeActivity;
import com.github.jnuutinen.cookbook.presentation.edit.EditRecipeActivity;
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
        AllRecipesFragment.RecipeFragmentListener {
    //private static final String TAG = MainActivity.class.getSimpleName();
    public static final String FILTER_RECIPE_ACTION = "com.github.jnuutinen.cookbook.FILTER_RECIPE";
    private static final int REQUEST_ADD_RECIPE = 1;
    public static final int REQUEST_EDIT_RECIPE = 2;
    public static final int REQUEST_VIEW_RECIPE = 3;

    @BindView(R.id.button_add) FloatingActionButton addButton;
    @BindView(R.id.button_add_category) FloatingActionButton addCategoryButton;
    @BindView(R.id.text_add_category) TextView addCategoryText;
    @BindView(R.id.button_add_recipe) FloatingActionButton addRecipeButton;
    @BindView(R.id.text_add_recipe) TextView addRecipeText;
    @BindView(R.id.tabs) TabLayout tabLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.container) ViewPager viewPager;

    private AlertDialog addCategoryDialog;
    private List<Category> categories;
    private AlertDialog deleteCategoryDialog;
    private AlertDialog deleteRecipeDialog;
    private AlertDialog editCategoryDialog;
    private Category editedCategory;
    private Recipe editedRecipe;
    private boolean isFabOpen = false;
    private Animation fabClose;
    private Animation fabOpen;
    private List<Recipe> recipes;
    private Animation rotateBackward;
    private Animation rotateForward;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        observe();

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mSectionsPagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        buildCreateCategoryDialog();
        buildDeleteCategoryDialog();
        buildDeleteRecipeDialog();
        buildEditCategoryDialog();
        checkFirstRun();
        getAnimations();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
                viewPager.setCurrentItem(0);
                Intent recipeFilterIntent = new Intent(FILTER_RECIPE_ACTION);
                recipeFilterIntent.putExtra("filter", newText);
                LocalBroadcastManager.getInstance(getBaseContext())
                        .sendBroadcast(recipeFilterIntent);
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
                    /*
                    Snackbar.make(toolbar, R.string.alert_recipe_saved, Snackbar.LENGTH_LONG)
                            .show();
                    */
                }
                break;
            case REQUEST_VIEW_RECIPE:
                if (resultCode == RESULT_OK) {
                    /*
                    Snackbar.make(toolbar, R.string.alert_recipe_deleted, Snackbar.LENGTH_LONG)
                            .show();
                    */
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
                viewPager.setCurrentItem(0);
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
    public void onRecipeEdit(Recipe recipe) {
        Intent intent = new Intent(this, EditRecipeActivity.class);
        intent.putExtra("recipe", recipe);
        startActivityForResult(intent, REQUEST_EDIT_RECIPE);
    }

    @Override
    public void onRecipeEdit(String name) {
        Recipe recipe = null;
        for (Recipe r : recipes) {
            if (r.getName().equals(name)) {
                recipe = r;
                break;
            }
        }
        if (recipe != null) {
            Intent intent = new Intent(this, EditRecipeActivity.class);
            intent.putExtra("recipe", recipe);
            startActivityForResult(intent, REQUEST_EDIT_RECIPE);
        }
    }

    @Override
    public void onRecipeDelete(Recipe recipe) {
        editedRecipe = recipe;
        deleteRecipeDialog.show();
    }

    @Override
    public void onRecipeDelete(String name) {
        Recipe recipe = null;
        for (Recipe r : recipes) {
            if (r.getName().equals(name)) {
                recipe = r;
                break;
            }
        }
        if (recipe != null) {
            onRecipeDelete(recipe);
        }
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
            onRecipeSelected(recipe);
        }
    }

    @Override
    public void onRecipeShare(Recipe recipe) {
        StringBuilder builder = new StringBuilder();
        builder.append(recipe.getName());
        builder.append("\n");
        builder.append("\n");
        if (recipe.getCategoryId() != null) {
            for (Category c : categories) {
                if (c.getId().equals(recipe.getCategoryId())) {
                    builder.append(c.getName());
                    builder.append("\n");
                    builder.append("\n");
                }
            }
        }
        for (String ingredient : recipe.getIngredients()) {
            builder.append(ingredient);
            builder.append("\n");
        }
        builder.append("\n");
        builder.append("\n");
        builder.append(recipe.getInstructions());
        String shareableRecipe = builder.toString();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, shareableRecipe);
        intent.setType("text/plain");
        String title = getResources().getString(R.string.action_share);
        Intent chooser = Intent.createChooser(intent, title);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    @Override
    public void onRecipeShare(String name) {
        Recipe recipe = null;
        for (Recipe r : recipes) {
            if (r.getName().equals(name)) {
                recipe = r;
                break;
            }
        }
        if (recipe != null) {
            onRecipeShare(recipe);
        }
    }

    @OnClick(R.id.button_add)
    void toggleButtons() {
        if(isFabOpen){
            addButton.startAnimation(rotateBackward);
            addRecipeText.startAnimation(fabClose);
            addCategoryText.startAnimation(fabClose);
            addRecipeButton.startAnimation(fabClose);
            addCategoryButton.startAnimation(fabClose);
            addRecipeButton.setClickable(false);
            addCategoryButton.setClickable(false);
            isFabOpen = false;
        } else {
            addButton.startAnimation(rotateForward);
            addRecipeText.startAnimation(fabOpen);
            addCategoryText.startAnimation(fabOpen);
            addRecipeButton.startAnimation(fabOpen);
            addCategoryButton.startAnimation(fabOpen);
            addRecipeButton.setClickable(true);
            addCategoryButton.setClickable(true);
            isFabOpen = true;
        }
    }

    @OnClick(R.id.button_add_category)
    void addCategory() {
        toggleButtons();
        editCategoryDialog.dismiss();
        deleteCategoryDialog.dismiss();
        addCategoryDialog.show();
    }

    @OnClick(R.id.button_add_recipe)
    void addRecipe() {
        toggleButtons();
        startActivityForResult(new Intent(this, CreateRecipeActivity.class),
                REQUEST_ADD_RECIPE);
    }


    @SuppressLint("InflateParams")
    private void buildCreateCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View createDialogView = getLayoutInflater().inflate(R.layout.dialog_create_category, null);
        addCategoryDialog = builder.setView(createDialogView)
                .setTitle(R.string.title_create_category)
                .setPositiveButton(R.string.action_save, (dialog, which) -> {
                    String categoryName = ((EditText) createDialogView
                            .findViewById(R.id.edit_category_name)).getText().toString().trim();
                    if (categoryName.length() == 0) {
                        Snackbar.make(toolbar, R.string.alert_blank_category_name,
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        // Check for duplicate category
                        boolean duplicateFound = false;
                        for (Category c : categories) {
                            if (c.getName().toLowerCase().equals(categoryName.toLowerCase())) {
                                duplicateFound = true;
                                Snackbar.make(toolbar, R.string.category_name_duplicate,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            }
                        }
                        if (!duplicateFound) {
                            viewModel.insertCategory(new Category(categoryName));
                            /*
                            Snackbar.make(toolbar, R.string.alert_category_saved,
                                    Snackbar.LENGTH_LONG).show();
                            */
                        }
                    }
                    ((EditText) createDialogView.findViewById(R.id.edit_category_name)).setText("");
                }).setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Canceled, do nothing
                }).create();
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

    private void buildDeleteRecipeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_delete_recipe);
        builder.setPositiveButton(R.string.yes, (dialog, id) -> viewModel.deleteRecipe(editedRecipe));
        builder.setNegativeButton(R.string.cancel, (dialog, d) -> {
            // 'Cancel' selected, do nothing
        });
        deleteRecipeDialog = builder.create();
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
                            /*
                            Snackbar.make(toolbar, R.string.alert_category_saved,
                                    Snackbar.LENGTH_LONG).show();
                            */
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

    private void getAnimations() {
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
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
