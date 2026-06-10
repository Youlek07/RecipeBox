package com.example.recipebox.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.recipebox.domain.model.RecipeRepository;

public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final RecipeRepository repository;

    public HomeViewModelFactory(RecipeRepository repository) {
        this.repository = repository;
    }

    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new HomeViewModel(repository);
    }
}
