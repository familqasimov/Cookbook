package com.github.jnuutinen.cookbook.presentation.editcategory;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.github.jnuutinen.cookbook.R;
import com.github.jnuutinen.cookbook.data.db.entity.Category;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditCategoryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.edit_category_name) EditText editTextName;

    private AlertDialog deleteDialog;
    private EditCategoryViewModel viewModel;
    private Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = ViewModelProviders.of(this).get(EditCategoryViewModel.class);

        category = getIntent().getParcelableExtra("category");
        setTitle(category.getName());
        editTextName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        editTextName.setText(category.getName());
        buildDeleteDialog();
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
            updateCategory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_delete_category)
    void deleteCategory() {
        deleteDialog.show();
    }

    private void buildDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_delete_category);
        builder.setPositiveButton(R.string.yes, (dialog, id) -> {
            viewModel.deleteCategory(category);
            setResult(RESULT_OK, new Intent());
            finish();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, d) -> {
            // 'Cancel' selected, do nothing
        });
        deleteDialog = builder.create();
    }

    private void updateCategory() {
        String categoryName = editTextName.getText().toString().trim();
        if (categoryName.length() == 0) {
            Snackbar.make(editTextName, R.string.alert_blank_category_name, Snackbar.LENGTH_LONG)
                    .show();
        } else {
            category.setName(categoryName);
            viewModel.updateCategory(category);
            setResult(RESULT_FIRST_USER, new Intent());
            finish();
        }
    }

}
