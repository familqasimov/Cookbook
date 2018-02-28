package com.github.jnuutinen.cookbook.presentation.editcategory;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.entity.Category;

import java.util.List;

public class EditCategoryViewModel extends AndroidViewModel {
    private DataRepository dataRepository;
    private LiveData<List<Category>> observableCategories;

    public EditCategoryViewModel(@NonNull Application application) {
        super(application);
        dataRepository = ((CookbookApp) application).getRepository();
        observableCategories = dataRepository.getLiveCategories();
    }

    void deleteCategory(Category category) {
        dataRepository.deleteCategory(category);
    }

    LiveData<List<Category>> getCategories() {
        return observableCategories;
    }

    void updateCategory(Category category) {
        dataRepository.updateCategory(category);
    }
}
