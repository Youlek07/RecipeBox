package com.example.recipebox.domain.usecase;

import com.example.recipebox.domain.model.Ingredient;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.ShoppingItem;
import com.example.recipebox.domain.model.ShoppingList;
import java.util.ArrayList;
import java.util.List;

public class ScaleRecipeUseCase {

    public List<Ingredient> execute(Recipe recipe, int targetServings) {
        validateRecipe(recipe);
        if (targetServings <= 0) {
            throw new IllegalArgumentException("Number of servings must be greater than zero, given: " + targetServings);
        }
        return recipe.scaleIngredients(targetServings);
    }

    public ShoppingList generateShoppingList(Recipe recipe, int targetServings, String listName) {
        List<Ingredient> scaled = execute(recipe, targetServings);

        List<ShoppingItem> items = new ArrayList<>();
        for (int i = 0; i < scaled.size(); i++) {
            Ingredient ing = scaled.get(i);
            ShoppingItem item = new ShoppingItem(
                    0,
                    ing.getName(),
                    ing.format(),
                    false,
                    0
            );
            items.add(item);
        }

        ShoppingList list = new ShoppingList();
        list.setName(listName != null ? listName : recipe.getName() + " x" + targetServings);
        list.setItems(items);
        return list;
    }

    public int getTotalTime(Recipe recipe) {
        validateRecipe(recipe);
        return recipe.getTotalTimeMinutes();
    }

    public ValidationResult validateForSave(Recipe recipe) {
        if (recipe == null) {
            return ValidationResult.error("Recipe cannot be null");
        }
        if (recipe.getName() == null || recipe.getName().trim().isEmpty()) {
            return ValidationResult.error("Recipe name is required");
        }
        if (recipe.getName().trim().length() < 3) {
            return ValidationResult.error("Recipe name must be at least 3 characters");
        }
        if (recipe.getServings() <= 0) {
            return ValidationResult.error("Number of servings must be greater than zero");
        }
        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            return ValidationResult.error("Recipe must contain at least one ingredient");
        }
        if (recipe.getSteps() == null || recipe.getSteps().isEmpty()) {
            return ValidationResult.error("Recipe must contain at least one step");
        }
        return ValidationResult.success();
    }

    private void validateRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe cannot be null");
        }
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() { return valid; }
        public String getErrorMessage() { return errorMessage; }
    }
}
