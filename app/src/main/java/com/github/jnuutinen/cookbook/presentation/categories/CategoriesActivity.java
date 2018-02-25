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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class CategoriesActivity extends AppCompatActivity {
    private static final int REQUEST_EDIT_CATEGORY = 1;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_EDIT_CATEGORY:
                /*
                RESULT_OK == category was deleted
                RESULT_FIRST_USER == category was edited
                 */
                if (resultCode == RESULT_OK) {
                    Snackbar.make(categoriesList, R.string.alert_category_deleted,
                            Snackbar.LENGTH_LONG).show();
                } else if (resultCode == RESULT_FIRST_USER) { // TODO: custom result code
                    Snackbar.make(categoriesList, R.string.alert_category_saved,
                            Snackbar.LENGTH_LONG).show();
                }
                break;
        }
    }

    @OnClick(R.id.button_add_category)
    void addCategory() {
        addCategoryDialog.show();
    }

    @OnItemClick(R.id.list_categories)
    void editCategory(int position) {
        Intent intent = new Intent(this, EditCategoryActivity.class);
        intent.putExtra("category", liveCategories.get(position));
        startActivityForResult(intent, REQUEST_EDIT_CATEGORY);
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
                        // Check for duplicate category
                        boolean duplicateFound = false;
                        for (Category c : liveCategories) {
                            if (c.getName().toLowerCase().equals(categoryName.toLowerCase())) {
                                duplicateFound = true;
                                Snackbar.make(categoriesList, R.string.category_name_duplicate,
                                        Snackbar.LENGTH_LONG).show();
                                break;
                            }
                        }
                        if (!duplicateFound) {
                            viewModel.insertCategory(new Category(categoryName));
                            Snackbar.make(categoriesList, R.string.alert_category_saved,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                    ((EditText) dialogView.findViewById(R.id.edit_category_name)).setText("");
                }).setNegativeButton(R.string.cancel, (dialog, which) -> {
                    // Canceled, do nothing
                }).create();
    }

    private void observe() {
        viewModel = ViewModelProviders.of(this).get(CategoriesViewModel.class);
        viewModel.getCategories().observe(this, categories -> {
            liveCategories = sortCategories(categories);
            categoriesList.setAdapter(new CategoryAdapter(this, liveCategories));
        });
    }

    private List<Category> sortCategories(List<Category> toBeSorted) {
        Comparator<Category> nameOrder = (entry1, entry2) -> {
            final String name1 = entry1.getName();
            final String name2 = entry2.getName();
            return name1.compareToIgnoreCase(name2);
        };
        Collections.sort(toBeSorted, nameOrder);
        return toBeSorted;
    }

}
