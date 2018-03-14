package com.github.jnuutinen.cookbook.presentation.create;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.CategorySpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO: cache unsaved recipe?
public class CreateRecipeActivity extends AppCompatActivity {
    //private static final String TAG = CreateRecipeActivity.class.getSimpleName();
    public static final String STATE_NAME = "name";
    public static final String STATE_INGREDIENTS = "ingredients";
    public static final String STATE_INSTRUCTIONS = "instructions";

    private final String PREFS_NAME = "com.github.jnuutinen.cookbook";
    private final String PREF_INGREDIENT_DELETE_DISMISS = "ingredient_delete_dismiss";

    @BindView(R.id.table_ingredients) TableLayout table;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.edit_name) EditText editTextName;
    @BindView(R.id.edit_instructions) EditText editTextInstructions;
    @BindView(R.id.checkbox_category) CheckBox categoryCheckBox;
    @BindView(R.id.spinner_category) Spinner spinnerCategory;

    private AlertDialog deleteIngredientDialog;
    private boolean ingredientAlertDismissed = false;
    private CreateRecipeViewModel viewModel;
    private ArrayList<String> ingredients;
    private List<Recipe> recipes;
    private Integer categoryId;
    private String name;
    private String instructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        observe();
        createIngredientDeletionDialog();
        checkPrefs();
        categoryCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                spinnerCategory.setVisibility(View.VISIBLE);
            } else {
                spinnerCategory.setVisibility(View.GONE);
            }
        });

        if (savedInstanceState != null) {
            name = savedInstanceState.getString(STATE_NAME);
            ingredients = savedInstanceState.getStringArrayList(STATE_INGREDIENTS);
            instructions = savedInstanceState.getString(STATE_INSTRUCTIONS);
            populate();
        } else {
            newRow(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_recipe, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        getRecipeInfo();
        savedInstanceState.putString(STATE_NAME, name);
        // TODO: persist category selection
        savedInstanceState.putStringArrayList(STATE_INGREDIENTS, ingredients);
        savedInstanceState.putString(STATE_INSTRUCTIONS, instructions);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            saveRecipe();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteIngredient(View view) {
        if (ingredientAlertDismissed) {
            removeRow();
        } else if (table.getChildCount() > 1) {
            deleteIngredientDialog.show();
        }
    }

    public void newRow(View view) {
        newRow(true);
    }

    private void checkPrefs() {
        final boolean DOESNT_EXIST = false;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean dismissed = prefs.getBoolean(PREF_INGREDIENT_DELETE_DISMISS, DOESNT_EXIST);

        if (dismissed) {
            ingredientAlertDismissed = true;
        }
    }

    private void createIngredientDeletionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View view = getLayoutInflater()
                .inflate(R.layout.dialog_delete_ingredient, null);
        builder.setView(view).setTitle(R.string.title_delete_ingredient)
                .setMessage(R.string.alert_delete_ingredient)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    if (((CheckBox)view.findViewById(R.id.checkbox_dont_show_dialog)).isChecked()) {
                        writePrefs();
                    }
                    removeRow();
                }).setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Cancel, do nothing
                });
        deleteIngredientDialog = builder.create();
    }

    private void getRecipeInfo() {
        ingredients = new ArrayList<>();
        if (categoryCheckBox.isChecked()) {
            categoryId = ((Category) spinnerCategory.getSelectedItem()).getId();
        } else {
            categoryId = null;
        }
        name = editTextName.getText().toString().trim();
        instructions = editTextInstructions.getText().toString().trim();
        for (int i = 0; i < table.getChildCount(); i++) {
            View view = table.getChildAt(i);
            TableRow row = (TableRow) view;
            if (((EditText) row.getChildAt(0)).getText().toString().trim().length() == 0) {
                continue;
            }
            ingredients.add(((EditText) row.getChildAt(0)).getText().toString().trim());
        }
    }

    private void newRow(boolean tapped) {
        // Table row
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        row.setLayoutParams(lp);

        // Ingredient EditText
        EditText ingredient = new EditText(this);
        lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
        ingredient.setLayoutParams(lp);
        ingredient.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        ingredient.setMaxLines(2);
        row.addView(ingredient);
        if (tapped) {
            ingredient.requestFocus();
        }
        table.addView(row);
    }

    private void observe() {
        viewModel = ViewModelProviders.of(this).get(CreateRecipeViewModel.class);
        viewModel.getCategories().observe(this, data -> {
            if (data == null || data.size() == 0) {
                categoryCheckBox.setEnabled(false);
            }
            spinnerCategory.setAdapter(new CategorySpinnerAdapter(this, data));
        });
        viewModel.getRecipes().observe(this, data -> recipes = data);
    }

    private void populate() {
        editTextName.setText(name);
        editTextInstructions.setText(instructions);

        // Ingredient rows
        for (int i = 0; i < ingredients.size(); i++) {
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(16, 16, 16, 16);
            TableRow row = new TableRow(this);
            row.setLayoutParams(lp);
            EditText ingredient = new EditText(this);
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.2f);
            ingredient.setLayoutParams(lp);
            ingredient.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            ingredient.setMaxLines(2);
            ingredient.setText(ingredients.get(i));
            row.addView(ingredient);
            table.addView(row);
        }
        // If there are no ingredients, create one empty row.
        if (table.getChildCount() == 0) newRow(new View(this));
    }

    private void removeRow() {
        int numberOfRows = table.getChildCount();
        if (numberOfRows > 1) {
            table.removeViewAt(numberOfRows - 1);
        }
    }

    private void saveRecipe() {
        name = editTextName.getText().toString().trim();
        if (name.length() == 0) {
            Snackbar.make(editTextName, R.string.alert_blank_recipe_name, Snackbar.LENGTH_LONG)
                    .show();
        } else {
            // Check for duplicate name
            boolean duplicateFound = false;
            for (Recipe r : recipes) {
                if (r.getName().toLowerCase().equals(name.toLowerCase())) {
                    duplicateFound = true;
                    break;
                }
            }
            if (duplicateFound) {
                Snackbar.make(editTextName, R.string.recipe_name_duplicate, Snackbar.LENGTH_LONG)
                        .show();
            } else {
                getRecipeInfo();
                Recipe recipe = new Recipe(name, categoryId, ingredients, instructions);
                viewModel.insertRecipe(recipe);
                setResult(RESULT_OK, new Intent());
                finish();
            }
        }
    }

    private void writePrefs() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(PREF_INGREDIENT_DELETE_DISMISS, true).apply();
        ingredientAlertDismissed = true;
    }
}
