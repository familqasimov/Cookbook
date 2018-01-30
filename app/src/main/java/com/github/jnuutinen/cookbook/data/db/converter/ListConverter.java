package com.github.jnuutinen.cookbook.data.db.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ListConverter {
    @TypeConverter
    public static List<String> fromString(String json) {
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }
}
