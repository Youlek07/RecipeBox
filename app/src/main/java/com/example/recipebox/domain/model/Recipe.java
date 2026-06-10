package com.example.recipebox.domain.model;

import java.util.List;
import java.util.Objects;

public class Recipe {

    public enum Difficulty {
        EASY, MEDIUM, HARD;

        public String getLabel() {
            switch (this) {
                case EASY: return "Easy";
                case MEDIUM: return "Medium";
                case HARD: return "Hard";
                default: return "Unknown";
            }
        }
    }

    private long id;
    private String name;
    private String description;
    private String imageUrl;
    private int servings;
    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private Difficulty difficulty;
    private String category;
    private List<Ingredient> ingredients;
    private List<String> steps;
    private boolean isLocal;

    public Recipe() {}

    public Recipe(long id, String name, String description, String imageUrl,
                  int servings, int prepTimeMinutes, int cookTimeMinutes,
                  Difficulty difficulty, String category,
                  List<Ingredient> ingredients, List<String> steps,
                  boolean isLocal) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.servings = servings;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
        this.difficulty = difficulty;
        this.category = category;
        this.ingredients = ingredients;
        this.steps = steps;
        this.isLocal = isLocal;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public int getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public void setCookTimeMinutes(int cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }

    public int getTotalTimeMinutes() {
        return prepTimeMinutes + cookTimeMinutes;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }

    public List<Ingredient> scaleIngredients(int targetServings) {
        if (targetServings <= 0) {
            throw new IllegalArgumentException("Number of servings must be greater than zero");
        }
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalStateException("Recipe does not contain ingredients");
        }
        if (servings <= 0) {
            throw new IllegalStateException("Base servings must be greater than zero");
        }

        double factor = (double) targetServings / servings;
        List<Ingredient> scaled = new java.util.ArrayList<>();
        for (Ingredient ing : ingredients) {
            Ingredient copy = ing.scaledBy(factor).getSmartUnit();
            scaled.add(copy);
        }
        return scaled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe)) return false;
        Recipe recipe = (Recipe) o;
        return id == recipe.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", servings=" + servings +
                '}';
    }
}
