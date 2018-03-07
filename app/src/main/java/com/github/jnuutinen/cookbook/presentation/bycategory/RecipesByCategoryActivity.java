package com.github.jnuutinen.cookbook.presentation.bycategory;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;
import com.github.jnuutinen.cookbook.presentation.main.MainActivity;
import com.github.jnuutinen.cookbook.presentation.viewrecipe.ViewRecipeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

import static com.github.jnuutinen.cookbook.presentation.main.MainActivity.REQUEST_VIEW_RECIPE;

public class RecipesByCategoryActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.text_no_recipes) TextView noRecipesText;
    @BindView(R.id.list_recipes_in_category) ListView list;

    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_by_category);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category = getIntent().getParcelableExtra("category");
        setTitle(category.getName());
        observe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_VIEW_RECIPE:
                if (resultCode == RESULT_OK) {
                    Snackbar.make(list, R.string.alert_recipe_deleted, Snackbar.LENGTH_LONG)
                            .show();
                }
                break;
        }
    }

    @OnItemClick(R.id.list_recipes_in_category)
    void viewRecipe(int position) {
        Intent intent = new Intent(this, ViewRecipeActivity.class);
        intent.putExtra("recipe", (Recipe)list.getAdapter().getItem(position));
        startActivityForResult(intent, REQUEST_VIEW_RECIPE);
    }

    private void observe() {
        ViewModelProviders.of(this).get(RecipesByCategoryViewModel.class)
                .getLiveRecipesByCategory(category).observe(this, data -> {
                    if (data == null || data.size() == 0) {
                        noRecipesText.setVisibility(View.VISIBLE);
                    } else {
                        noRecipesText.setVisibility(View.GONE);
                    }
                    list.setAdapter(new PlainRecipeAdapter(this, data));
        });
    }

}
