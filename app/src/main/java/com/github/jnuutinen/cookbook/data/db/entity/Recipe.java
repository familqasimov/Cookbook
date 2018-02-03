package com.github.jnuutinen.cookbook.data.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Nullable;

import java.util.List;

import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(foreignKeys = @ForeignKey(entity = Category.class,
                                  parentColumns = "id",
                                  childColumns = "category_id",
                                  onDelete = SET_NULL))
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String name;

    @Nullable
    @ColumnInfo(name = "category_id")
    private Integer categoryId;

    private List<String> ingredients;

    private String instructions;

    public Recipe() {
    }

    @Ignore
    public Recipe(String name, @Nullable Integer categoryId, List<String> ingredients, String instructions) {
        this.name = name;
        this.categoryId = categoryId;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) { this.id = id; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@Nullable Integer categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
