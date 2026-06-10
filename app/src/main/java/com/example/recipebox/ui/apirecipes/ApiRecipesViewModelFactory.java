package com.example.recipebox.ui.apirecipes;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.recipebox.domain.model.RecipeRepository;

public class ApiRecipesViewModelFactory implements ViewModelProvider.Factory {
    private final RecipeRepository repository;
    public ApiRecipesViewModelFactory(RecipeRepository r) { this.repository = r; }
    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> c) {
        return (T) new ApiRecipesViewModel(repository);
    }
}
