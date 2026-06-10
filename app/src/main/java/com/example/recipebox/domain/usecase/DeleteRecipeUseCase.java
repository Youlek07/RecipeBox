package com.example.recipebox.domain.usecase;

import com.example.recipebox.domain.model.RecipeRepository;

public class DeleteRecipeUseCase {
    private final RecipeRepository repository;

    public DeleteRecipeUseCase(RecipeRepository repository) {
        this.repository = repository;
    }

    public void execute(long recipeId) {
        if (recipeId <= 0) {
            throw new IllegalArgumentException("Invalid recipe ID");
        }
        repository.deleteRecipe(recipeId);
    }
}
