package com.github.jnuutinen.cookbook.presentation.editcategory;


import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.entity.Category;

public class EditCategoryViewModel extends AndroidViewModel {
    private DataRepository dataRepository;

    public EditCategoryViewModel(@NonNull Application application) {
        super(application);
        dataRepository = ((CookbookApp) application).getRepository();
    }

    void deleteCategory(Category category) {
        dataRepository.deleteCategory(category);
    }

    void updateCategory(Category category) {
        dataRepository.updateCategory(category);
    }
}
