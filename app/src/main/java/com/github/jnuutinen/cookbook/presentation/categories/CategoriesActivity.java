package com.github.jnuutinen.cookbook.presentation.categories;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.presentation.editcategory.EditCategoryActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class CategoriesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.list_categories) ListView categoriesList;

    private AlertDialog addCategoryDialog;
    private CategoriesViewModel viewModel;
    private List<Category> liveCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buildCreateCategoryDialog();
        observe();
    }

    @OnClick(R.id.button_add_category)
    void addCategory() {
        addCategoryDialog.show();
    }

    @OnItemClick(R.id.list_categories)
    void editCategory(int position) {
        Intent intent = new Intent(this, EditCategoryActivity.class);
        intent.putExtra("category", liveCategories.get(position));
        startActivity(intent);
    }

    private void buildCreateCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        @SuppressLint("InflateParams") View dialogView = getLayoutInflater()
                .inflate(R.layout.dialog_create_category, null);
        addCategoryDialog = builder.setView(dialogView)
                .setMessage(R.string.title_create_category)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    String categoryName = ((EditText) dialogView
                            .findViewById(R.id.edit_category_name)).getText().toString().trim();
                    if (categoryName.length() == 0) {
                        Snackbar.make(categoriesList, R.string.alert_blank_category_name,
                                Snackbar.LENGTH_LONG).show();
                    } else {
                        viewModel.insertCategory(new Category(categoryName));
                    }
                }).create();
    }

    private void observe() {
        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        viewModel.getCategories().observe(this, categories -> {
            liveCategories = categories;
            categoriesList.setAdapter(new CategoryAdapter(this, categories));
        });
    }

}
