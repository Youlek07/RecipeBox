package com.example.recipebox.domain.model;

import java.util.List;

public interface RecipeRepository {

    List<Recipe> getAllLocalRecipes();
    Recipe getLocalRecipeById(long id);
    long insertRecipe(Recipe recipe);
    void updateRecipe(Recipe recipe);
    void deleteRecipe(long id);

    void searchRecipes(String query, RecipeCallback<List<Recipe>> callback);
    void getRecipeDetail(int apiId, RecipeCallback<Recipe> callback);

    interface RecipeCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }
}
