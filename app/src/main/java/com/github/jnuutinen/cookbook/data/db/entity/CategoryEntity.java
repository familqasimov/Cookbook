package com.github.jnuutinen.cookbook.data.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.github.jnuutinen.cookbook.data.model.Category;

@Entity
public class CategoryEntity implements Category {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    private String name;

    public CategoryEntity() {
    }

    @Ignore
    public CategoryEntity(String name) {
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setId(Integer id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }
}
