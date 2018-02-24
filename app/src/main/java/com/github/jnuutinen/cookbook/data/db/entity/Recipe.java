package com.github.jnuutinen.cookbook.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_DEFAULT;

@Entity(foreignKeys = @ForeignKey(entity = Category.class,
                                  parentColumns = "id",
                                  childColumns = "categoryId",
                                  onUpdate = CASCADE,
                                  onDelete = SET_DEFAULT))
public class Recipe implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    private Integer categoryId = 0;
    private List<String> ingredients;
    private String instructions;

    public Recipe() {
    }

    @Ignore
    public Recipe(String name, Integer categoryId, List<String> ingredients, String instructions) {
        this.name = name;
        this.categoryId = categoryId;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    @Ignore
    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        categoryId = in.readInt();
        ingredients = new ArrayList<>();
        in.readStringList(ingredients);
        instructions = in.readString();
    }

    public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(categoryId);
        dest.writeStringList(ingredients);
        dest.writeString(instructions);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
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
