package com.github.jnuutinen.cookbook.presentation.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.edit.EditRecipeActivity;
import com.github.jnuutinen.cookbook.presentation.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewRecipeActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.button_favorite) FloatingActionButton favoriteButton;
    @BindView(R.id.text_view_category) TextView category;
    @BindView(R.id.text_view_ingredients) TextView ingredients;
    @BindView(R.id.text_view_instructions) TextView instructions;

    private AlertDialog deleteRecipeDialog;
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

        buildDeleteRecipeDialog();
        //noinspection ConstantConditions
        recipe = getIntent().getParcelableExtra("recipe");
        observe();
        setTitle(recipe.getName());
        getRecipe();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recipe == null) {
            recipe = viewModel.getCachedRecipe();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.cacheRecipe(recipe);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_recipe, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.REQUEST_EDIT_RECIPE:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(category, R.string.alert_recipe_saved, Snackbar.LENGTH_LONG).show();
                }
                recipe = data.getParcelableExtra("recipe");
                getRecipe();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_delete_recipe:
                deleteRecipeDialog.show();
                return true;

            case R.id.action_edit_recipe:
                viewModel.cacheRecipe(recipe);
                Intent intent = new Intent(this, EditRecipeActivity.class);
                intent.putExtra("recipe", recipe);
                startActivityForResult(intent, MainActivity.REQUEST_EDIT_RECIPE);
                return true;
            case R.id.action_share:
                shareRecipe();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.button_favorite)
    void toggleFavorite() {
        if (recipe.getIsFavorite() == 1) {
            recipe.setIsFavorite(0);
            favoriteButton.setImageResource(R.drawable.ic_action_favorite_border);
        } else {
            recipe.setIsFavorite(1);
            favoriteButton.setImageResource(R.drawable.ic_action_favorite);
        }
        viewModel.updateRecipe(recipe);
    }

    private void buildDeleteRecipeDialog() {
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
        deleteRecipeDialog = builder.create();
    }

    private void getRecipe() {
        // Category is set in observe(), when LiveData is fetched
        ingredients.setText("");
        for (int i = 0; i < recipe.getIngredients().size(); i++) {
            ingredients.append(recipe.getIngredients().get(i) + "\n");
        }
        instructions.setText(recipe.getInstructions());
        if (recipe.getIsFavorite() == 1) {
            favoriteButton.setImageResource(R.drawable.ic_action_favorite);
        }
    }

    private void observe() {
        viewModel.getCombinedRecipes().observe(this, liveCombinedRecipes -> {
            if (liveCombinedRecipes != null) {
                for (CombineDao.combinedRecipe combined : liveCombinedRecipes) {
                    if (combined.recipeName.equals(recipe.getName())) {
                        if (combined.categoryName == null) {
                            category.setText(R.string.recipe_no_category);
                        } else {
                            category.setText(combined.categoryName);
                        }
                    }
                }
            }
        });
    }

    private void shareRecipe() {
        StringBuilder builder = new StringBuilder();
        builder.append(recipe.getName());
        builder.append("\n");
        builder.append("\n");
        if (recipe.getCategoryId() != null) {
            builder.append(category.getText().toString());
            builder.append("\n");
            builder.append("\n");
        }
        builder.append(ingredients.getText().toString());
        builder.append("\n");
        builder.append("\n");
        builder.append(instructions.getText().toString());
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
}
