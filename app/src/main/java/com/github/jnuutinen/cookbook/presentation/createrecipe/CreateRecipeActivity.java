package com.github.jnuutinen.cookbook.presentation.createrecipe;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.CategorySpinnerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

// TODO: cache unsaved recipe?
public class CreateRecipeActivity extends AppCompatActivity {
    //private static final String TAG = CreateRecipeActivity.class.getSimpleName();

    @BindView(R.id.table_ingredients) TableLayout table;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.edit_name) EditText editTextName;
    @BindView(R.id.edit_instructions) EditText editTextInstructions;
    @BindView(R.id.spinner_category) Spinner spinnerCategory;

    private CreateRecipeViewModel viewModel;
    private ArrayList<String> ingredients;
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
        viewModel = ViewModelProviders.of(this).get(CreateRecipeViewModel.class);
        populateCategoriesSpinner();
        newRow();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_create_recipe, menu);
        return true;
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

    public void newRow(View view) {
        newRow();
    }

    public void removeRow(View view) {
        int numberOfRows = table.getChildCount();
        if (numberOfRows > 1) table.removeViewAt(numberOfRows - 1);
    }

    private void getRecipeInfo() {
        ingredients = new ArrayList<>();
        categoryId = ((Category) spinnerCategory.getSelectedItem()).getId();
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

    private void newRow() {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        lp.setMargins(16, 16, 16, 16);
        row.setLayoutParams(lp);
        EditText ingredient = new EditText(this);
        lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.6f);
        ingredient.setLayoutParams(lp);
        ingredient.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        ingredient.setMaxLines(2);
        row.addView(ingredient);
        table.addView(row);
    }

    private void populateCategoriesSpinner() {
        viewModel.getCategories().observe(this, categories ->
                spinnerCategory.setAdapter(new CategorySpinnerAdapter(this, categories)));
    }

    private void saveRecipe() {
        if (editTextName.getText().toString().trim().length() == 0) {
            Snackbar.make(editTextName, R.string.alert_blank_recipe_name, Snackbar.LENGTH_LONG)
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