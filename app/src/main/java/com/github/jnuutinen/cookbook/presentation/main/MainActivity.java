package com.github.jnuutinen.cookbook.presentation.main;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.create.CreateRecipeActivity;
import com.github.jnuutinen.cookbook.presentation.view.ViewRecipeActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_ADD_RECIPE = 1;

    @BindView(R.id.list_recipes) ListView recipeList;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private List<Recipe> liveRecipes;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        observe();
        setListOnClick();
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
                //noinspection StatementWithEmptyBody
                if (resultCode == RESULT_OK) {
                    // TODO: recipe saved, display snackbar?
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_add_recipe)
    void addRecipe() {
        startActivityForResult(new Intent(this, CreateRecipeActivity.class),
                REQUEST_ADD_RECIPE);
    }

    /* TODO: butterknife onclick
    @OnItemClick(R.id.list_recipes)
    void viewRecipe(int position) {
        Intent intent = new Intent(this, ViewRecipeActivity.class);
        intent.putExtra("recipe", adapter.getItem(position));
        startActivity(intent);
    }
    */
    // TODO: onclick ei toimi
    private void setListOnClick() {
        recipeList.setOnItemClickListener((adapterView, view, i, l) -> {
            Log.e(TAG, "list onClick() entered");
            Intent intent = new Intent(this, ViewRecipeActivity.class);
            intent.putExtra("recipe", liveRecipes.get(i));
            startActivity(intent);
        });
    }

    private void observe() {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getRecipes().observe(this, recipes -> {
                    adapter = new RecipeAdapter(MainActivity.this, recipes);
                    recipeList.setAdapter(adapter);
                    liveRecipes = recipes;
                }
        );
    }
}
