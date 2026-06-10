package com.example.recipebox.ui.detail;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.recipebox.domain.model.RecipeRepository;
import com.example.recipebox.domain.model.ShoppingListRepository;

public class RecipeDetailViewModelFactory implements ViewModelProvider.Factory {

    private final RecipeRepository recipeRepository;
    private final ShoppingListRepository shoppingRepository;

    public RecipeDetailViewModelFactory(RecipeRepository recipeRepository,
                                        ShoppingListRepository shoppingRepository) {
        this.recipeRepository = recipeRepository;
        this.shoppingRepository = shoppingRepository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecipeDetailViewModel.class)) {
            return (T) new RecipeDetailViewModel(recipeRepository, shoppingRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass);
    }
}


