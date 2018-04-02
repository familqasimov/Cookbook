package com.github.jnuutinen.cookbook.data.db.entity;

import android.annotation.SuppressLint;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

@Entity(foreignKeys = @ForeignKey(entity = Category.class,      // Foreign key points to a category
                                  parentColumns = "id",         // 'id' column in category table
                                  childColumns = "category_id", // 'category_id' column in recipe
                                  onUpdate = CASCADE,           // If updated in category, update fk too
                                  onDelete = SET_NULL))         // If category deleted, set recipe fk null
public class Recipe implements Parcelable {
    private static final int NULL_INTEGER_FLAG = 0;
    private static final int NONNULL_INTEGER_FLAG = 1;

    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    @ColumnInfo(name = "category_id")
    private Integer categoryId;
    private List<String> ingredients;
    private String instructions;
    @ColumnInfo(name = "is_favorite")
    private int isFavorite = 0;

    public Recipe() {}

    @Ignore
    public Recipe(String name, Integer categoryId, List<String> ingredients, String instructions) {
        this.name = name;
        this.categoryId = categoryId;
        this.ingredients = ingredients;
        this.instructions = instructions;
        isFavorite = 0;
    }

    @SuppressLint("ParcelClassLoader")
    @Ignore
    private Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        categoryId = readInteger(in);
        ingredients = new ArrayList<>();
        in.readStringList(ingredients);
        instructions = in.readString();
        isFavorite = in.readInt();
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
        writeInteger(dest, categoryId);
        dest.writeStringList(ingredients);
        dest.writeString(instructions);
        dest.writeInt(isFavorite);
    }

    /**
     * Handles Nullable Integer writing to parcel
     * @param dest Parcel to write into
     * @param i Nullable Integer to write to parcel
     */
    private static void writeInteger(Parcel dest, Integer i) {
        if (i != null) {
            dest.writeInt(NONNULL_INTEGER_FLAG);
            dest.writeInt(i);
        }
        else {
            dest.writeInt(NULL_INTEGER_FLAG);
        }
    }

    /**
     * Handles Nullable Integer reading from parcel
     * @param in Parcel to read from
     * @return Nullable Integer read from parcel
     */
    private static Integer readInteger(Parcel in) {
        if (in.readInt() != NULL_INTEGER_FLAG) {
            return in.readInt();
        }
        else {
            return null;
        }
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

    public int getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(int isFavorite) {
        this.isFavorite = isFavorite;
    }
}
