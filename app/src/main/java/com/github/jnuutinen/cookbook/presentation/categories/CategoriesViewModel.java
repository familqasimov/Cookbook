package com.github.jnuutinen.cookbook.presentation.categories;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.entity.Category;

import java.util.List;


public class CategoriesViewModel extends AndroidViewModel {
    private final LiveData<List<Category>> observableCategories;
    private DataRepository dataRepository;

    public CategoriesViewModel(@NonNull Application application) {
        super(application);
        dataRepository = ((CookbookApp) application).getRepository();
        observableCategories = dataRepository.getLiveCategories();
    }

    LiveData<List<Category>> getCategories() {
        return observableCategories;
    }

    void insertCategory(Category category) {
        dataRepository.insertCategory(category);
    }

    void updateCategory(Category category) {
        dataRepository.updateCategory(category);
    }
}
