package com.github.jnuutinen.cookbook.presentation.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewRecipeActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_view_name)
    TextView name;
    @BindView(R.id.text_view_category)
    TextView category;
    @BindView(R.id.text_view_ingredients)
    TextView ingredients;
    @BindView(R.id.text_view_instructions)
    TextView instructions;

    private AlertDialog deleteDialog;
    private Recipe recipe;
    private ViewRecipeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewModel = ViewModelProviders.of(this).get(ViewRecipeViewModel.class);

        buildDeleteDialog();

        //noinspection ConstantConditions
        recipe = getIntent().getParcelableExtra("recipe");
        getRecipe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete_recipe:
                deleteDialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void buildDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_delete_recipe);
        builder.setPositiveButton(R.string.yes, (dialog, id) -> {
            viewModel.deleteRecipe(recipe);
            setResult(RESULT_OK, new Intent());
            finish();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, d) -> {
            // 'Cancel' selected, do nothing
        });
        deleteDialog = builder.create();
    }

    private void getRecipe() {
        name.setText(recipe.getName());
        category.setText(recipe.getCategory());
        ingredients.setText("");
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            ingredients.append(recipe.getIngredients().get(i) + "\n");
        }
        instructions.setText(recipe.getInstructions());
    }
}
