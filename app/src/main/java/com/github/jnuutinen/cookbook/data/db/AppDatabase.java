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
import com.github.jnuutinen.cookbook.data.db.dao.CombineDao;
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

    private static AppDatabase buildDatabase(final Context appContext) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DB_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        executors.diskIo().execute(() -> {
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);
                            List<Category> categories = DataGenerator.generateCategories();
                            insertCategories(database, categories);
                            List<Recipe> recipes = DataGenerator.generateRecipes(categories);
                            insertRecipes(database, recipes);
                            database.setDatabaseCreated();
                        });
                    }
                }).build();
    }

    private static void insertCategories(final AppDatabase database,
                                         final List<Category> categories) {
        database.runInTransaction(() -> database.categoryDao().insertAll(categories));
    }

    private static void insertRecipes(final AppDatabase database,
                                              final List<Recipe> recipes) {
        database.runInTransaction(() -> database.recipeDao().insertAll(recipes));
    }

    public abstract RecipeDao recipeDao();

    public abstract CategoryDao categoryDao();

    public abstract CombineDao combineDao();

    public LiveData<Boolean> getDatabaseCreated() {
        return isDatabaseCreated;
    }

    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DB_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        isDatabaseCreated.postValue(true);
    }

    public void deleteAllCategories() {
        executors.diskIo().execute(() -> instance.runInTransaction(() ->
                instance.categoryDao().deleteAll()));
    }

    public void deleteAllRecipes() {
        executors.diskIo().execute(() -> instance.runInTransaction(() ->
                instance.recipeDao().deleteAll()));
    }

    public void deleteCategory(final Category category) {
        executors.diskIo().execute(() -> instance.runInTransaction(() ->
                instance.categoryDao().delete(category)));
    }

    public void deleteRecipe(final Recipe recipe) {
        executors.diskIo().execute(() -> instance.runInTransaction(() ->
                instance.recipeDao().delete(recipe)));
    }

    public LiveData<List<Category>> getCategories() {
        return categoryDao().getAll();
    }

    public LiveData<List<CombineDao.combinedRecipe>> getFavoriteCombinedRecipes() {
        return combineDao().getFavoriteCombinedRecipes();
    }

    public LiveData<List<Recipe>> getFavoriteRecipes() {
        return recipeDao().getFavorites();
    }

    public LiveData<List<Recipe>> getRecipes() {
        return recipeDao().getAll();
    }

    public LiveData<List<Recipe>> getRecipes(Category category) {
        return recipeDao().getByCategory(category.getId());
    }

    public LiveData<List<CombineDao.combinedRecipe>> getCombinedRecipes() {
        return combineDao().getCombinedRecipes();
    }

    public void insertRecipe(final Recipe recipe) {
        executors.diskIo().execute(() -> instance.runInTransaction(() ->
                instance.recipeDao().insert(recipe)));
    }

    public void insertCategory(final Category category) {
        executors.diskIo().execute(() -> instance.runInTransaction(() ->
                instance.categoryDao().insert(category)));
    }

    public void updateCategory(final Category category) {
        executors.diskIo().execute(() -> instance.runInTransaction(() ->
                instance.categoryDao().update(category)));
    }

    public void updateRecipe(final Recipe recipe) {
        executors.diskIo().execute(() -> instance.runInTransaction(() ->
                instance.recipeDao().update(recipe)));
    }
}
