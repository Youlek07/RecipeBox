package com.example.recipebox.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.example.recipebox.data.local.Converters;
import java.util.List;

@Entity(tableName = "recipes")
@TypeConverters(Converters.class)
public class RecipeEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String description;
    public String imageUrl;
    public int servings;
    public int prepTimeMinutes;
    public int cookTimeMinutes;
    public String difficulty;
    public String category;
    public boolean isLocal;

    public List<IngredientEntity> ingredients;
    public List<String> steps;
}
