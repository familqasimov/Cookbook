package com.github.jnuutinen.cookbook.data.model;


import java.util.List;

public interface Recipe {
    Integer getId();
    String getName();
    Integer getCategoryId();
    List<String> getIngredients();
    String getInstructions();
}
