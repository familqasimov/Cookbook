package com.github.jnuutinen.cookbook.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.github.jnuutinen.cookbook.AppExecutors;
import com.github.jnuutinen.cookbook.data.db.converter.ListConverter;
import com.github.jnuutinen.cookbook.data.db.dao.CategoryDao;
import com.github.jnuutinen.cookbook.data.db.dao.RecipeDao;
import com.github.jnuutinen.cookbook.data.db.entity.Category;
import com.github.jnuutinen.cookbook.data.db.entity.Recipe;

import java.util.List;

@Database(entities = {Recipe.class, Category.class}, version = 1, exportSchema = false)
@TypeConverters({ListConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "cookbook_db";

    private static AppDatabase instance;
    private static AppExecutors executors;

    public abstract RecipeDao recipeDao();
    public abstract CategoryDao categoryDao();

    private final MutableLiveData<Boolean> isDatabaseCreated = new MutableLiveData<>();

    public static AppDatabase getInstance(final Context context, final AppExecutors appExecutors) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    executors = appExecutors;
                    instance = buildDatabase(context.getApplicationContext());
                    instance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return isDatabaseCreated;
    }

    public static void insertRecipe(final Recipe recipe) {
        executors.diskIo().execute(() -> instance.runInTransaction(() ->
                instance.recipeDao().insert(recipe)));
    }

    private static AppDatabase buildDatabase(final Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DB_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIo().execute(() -> {
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);
                            List<Category> categories = DataGenerator.generateCategories();
                            List<Recipe> recipes = DataGenerator.generateRecipes();
                            insertData(database, categories, recipes);
                            database.setDatabaseCreated();
                        });
                    }
                }).build();
    }

    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DB_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        isDatabaseCreated.postValue(true);
    }

    private static void insertData(final AppDatabase database,
                                   final List<Category> categories,
                                   final List<Recipe> recipes) {
        database.runInTransaction(() -> {
            database.categoryDao().insertAll(categories);
            database.recipeDao().insertAll(recipes);
        });
    }
}
