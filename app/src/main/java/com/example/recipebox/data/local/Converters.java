package com.example.recipebox.data.local;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.example.recipebox.data.local.entity.IngredientEntity;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromIngredientList(List<IngredientEntity> list) {
        return list == null ? null : gson.toJson(list);
    }

    @TypeConverter
    public static List<IngredientEntity> toIngredientList(String json) {
        if (json == null) return null;
        Type type = new TypeToken<List<IngredientEntity>>() {}.getType();
        return gson.fromJson(json, type);
    }

    @TypeConverter
    public static String fromStringList(List<String> list) {
        return list == null ? null : gson.toJson(list);
    }

    @TypeConverter
    public static List<String> toStringList(String json) {
        if (json == null) return null;
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }
}
