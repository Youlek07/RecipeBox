package com.example.recipebox.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.recipebox.domain.model.Ingredient;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.RecipeRepository;
import com.example.recipebox.domain.model.ShoppingList;
import com.example.recipebox.domain.model.ShoppingListRepository;
import com.example.recipebox.domain.usecase.ScaleRecipeUseCase;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeDetailViewModel extends ViewModel {

    private final RecipeRepository recipeRepository;
    private final ShoppingListRepository shoppingRepository;
    private final ScaleRecipeUseCase scaleUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Recipe> recipe = new MutableLiveData<>();
    private final MutableLiveData<List<Ingredient>> scaledIngredients = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentServings = new MutableLiveData<>(0);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> shoppingAdded = new MutableLiveData<>();

    public RecipeDetailViewModel(RecipeRepository recipeRepository,
                                 ShoppingListRepository shoppingRepository) {
        this.recipeRepository = recipeRepository;
        this.shoppingRepository = shoppingRepository;
        this.scaleUseCase = new ScaleRecipeUseCase();
    }


    public void loadRecipe(long id) {
        executor.execute(() -> {
            try {
                Recipe r = recipeRepository.getLocalRecipeById(id);
                if (r != null) {
                    recipe.postValue(r);
                    currentServings.postValue(r.getServings());
                    scaledIngredients.postValue(r.getIngredients());
                } else {
                    error.postValue("Nie znaleziono przepisu");
                }
            } catch (Exception e) {
                error.postValue("Loading error: " + e.getMessage());
            }
        });
    }


    public void scaleToServings(int targetServings) {
        Recipe r = recipe.getValue();
        if (r == null) return;
        try {
            List<Ingredient> scaled = scaleUseCase.execute(r, targetServings);
            scaledIngredients.setValue(scaled);
            currentServings.setValue(targetServings);
        } catch (Exception e) {
            error.setValue(e.getMessage());
        }
    }


    public void addToShoppingList() {
        Recipe r = recipe.getValue();
        Integer servings = currentServings.getValue();
        if (r == null || servings == null) return;

        executor.execute(() -> {
            try {
                ShoppingList list = scaleUseCase.generateShoppingList(
                        r, servings, r.getName() + " ×" + servings);
                shoppingRepository.insertList(list);
                shoppingAdded.postValue(true);
            } catch (Exception e) {
                error.postValue("Error adding to shopping list: " + e.getMessage());
            }
        });
    }


    public LiveData<Recipe> getRecipe() { return recipe; }
    public LiveData<List<Ingredient>> getScaledIngredients() { return scaledIngredients; }
    public LiveData<Integer> getCurrentServings() { return currentServings; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getShoppingAdded() { return shoppingAdded; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}

