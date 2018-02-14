package com.github.jnuutinen.cookbook.presentation.edit;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.CookbookApp;
import com.github.jnuutinen.cookbook.data.DataRepository;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;


class EditRecipeViewModel extends AndroidViewModel {
    private final MediatorLiveData<List<Category>> observableCategories;
    private DataRepository dataRepository;

    EditRecipeViewModel(@NonNull Application application) {
        super(application);
        observableCategories = new MediatorLiveData<>();
        dataRepository = ((CookbookApp) application).getRepository();
        LiveData<List<Category>> categories = dataRepository.getCategories();
        observableCategories.addSource(categories, observableCategories::setValue);
    }

    LiveData<List<Category>> getCategories() {
        return observableCategories;
    }

    void updateRecipe(Recipe recipe) {
        dataRepository.updateRecipe(recipe);
    }
}
