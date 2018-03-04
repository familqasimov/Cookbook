package com.github.jnuutinen.cookbook.presentation.editrecipe;

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

import static com.github.jnuutinen.cookbook.presentation.createrecipe.CreateRecipeActivity.STATE_INGREDIENTS;
import static com.github.jnuutinen.cookbook.presentation.createrecipe.CreateRecipeActivity.STATE_INSTRUCTIONS;
import static com.github.jnuutinen.cookbook.presentation.createrecipe.CreateRecipeActivity.STATE_NAME;

public class EditRecipeActivity extends AppCompatActivity {
//private static final String TAG = EditRecipeActivity.class.getSimpleName();

    @BindView(R.id.table_ingredients) TableLayout table;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.edit_name) EditText editTextName;
    @BindView(R.id.edit_instructions) EditText editTextInstructions;
    @BindView(R.id.spinner_category) Spinner spinnerCategory;
    @BindView(R.id.checkbox_category) CheckBox categoryCheckBox;

    private List<Category> liveCategories;
    private CategorySpinnerAdapter adapter;
    private Recipe recipe;
    private EditRecipeViewModel viewModel;
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

        viewModel = ViewModelProviders.of(this).get(EditRecipeViewModel.class);
        observe();
        recipe = getIntent().getParcelableExtra("recipe");
        categoryCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                spinnerCategory.setVisibility(View.VISIBLE);
            } else {
                spinnerCategory.setVisibility(View.GONE);
            }
        });
        setTitle(recipe.getName());
        if (savedInstanceState != null) {
            name = savedInstanceState.getString(STATE_NAME);
            ingredients = savedInstanceState.getStringArrayList(STATE_INGREDIENTS);
            instructions = savedInstanceState.getString(STATE_INSTRUCTIONS);
            populate(true);
        } else {
            populate(false);
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
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("recipe", recipe);
        setResult(RESULT_CANCELED, intent);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            updateRecipe();
            return true;
        } else if (id == android.R.id.home) {
            Intent intent = new Intent();
            intent.putExtra("recipe", recipe);
            setResult(RESULT_CANCELED, intent);
            finish();
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
        name = editTextName.getText().toString().trim();
        ingredients = new ArrayList<>();
        if (categoryCheckBox.isChecked()) {
            categoryId = ((Category) spinnerCategory.getSelectedItem()).getId();
        } else {
            categoryId = null;
        }
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
        ingredient.requestFocus();
        table.addView(row);
    }

    private void populate(boolean fromState) {
        int ingredientsSize;
        if (fromState) {
            editTextName.setText(name);
            editTextInstructions.setText(instructions);
            ingredientsSize = ingredients.size();
        } else {
            ingredientsSize = recipe.getIngredients().size();
            editTextName.setText(recipe.getName());
            editTextInstructions.setText(recipe.getInstructions());
            categoryId = recipe.getCategoryId();
            if (categoryId == null) {
                categoryCheckBox.setChecked(false);
                spinnerCategory.setVisibility(View.GONE);
            } else {
                categoryCheckBox.setChecked(true);
                spinnerCategory.setVisibility(View.VISIBLE);
            }
        }

        // Ingredient rows
        for (int i = 0; i < ingredientsSize; i++) {
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            lp.setMargins(16, 16, 16, 16);
            TableRow row = new TableRow(this);
            row.setLayoutParams(lp);
            EditText ingredient = new EditText(this);
            lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.2f);
            ingredient.setLayoutParams(lp);
            ingredient.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
            ingredient.setMaxLines(2);
            if (fromState) {
                ingredient.setText(ingredients.get(i));
            } else {
                ingredient.setText(recipe.getIngredients().get(i));
            }
            row.addView(ingredient);
            table.addView(row);
        }
        // If there are no ingredients, create one empty row.
        if (table.getChildCount() == 0) newRow(new View(this));
    }

    private void observe() {
        viewModel.getCategories().observe(this, categories -> {
            liveCategories = categories;
            adapter = new CategorySpinnerAdapter(this, categories);
            spinnerCategory.setAdapter(adapter);
            if (recipe.getCategoryId() != null) {
                categoryCheckBox.setChecked(true);
                spinnerCategory.setVisibility(View.VISIBLE);
                for (Category c : liveCategories) {
                    if ((int)c.getId() == recipe.getCategoryId()) {
                        spinnerCategory.setSelection(adapter.getPosition(c));
                        break;
                    }
                }
            }
        });
    }

    private void updateRecipe() {
        if (editTextName.getText().toString().trim().length() == 0) {
            Snackbar.make(editTextName, R.string.alert_blank_recipe_name, Snackbar.LENGTH_LONG)
                    .show();
        } else {
            getRecipeInfo();
            recipe.setName(name);
            recipe.setCategoryId(categoryId);
            recipe.setIngredients(ingredients);
            recipe.setInstructions(instructions);
            viewModel.updateRecipe(recipe);
            Intent intent = new Intent();
            intent.putExtra("recipe", recipe);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
