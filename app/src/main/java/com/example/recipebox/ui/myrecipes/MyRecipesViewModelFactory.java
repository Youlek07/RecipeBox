package com.example.recipebox.ui.myrecipes;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.recipebox.domain.model.RecipeRepository;

public class MyRecipesViewModelFactory implements ViewModelProvider.Factory {

    private final RecipeRepository repository;

    public MyRecipesViewModelFactory(RecipeRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MyRecipesViewModel.class)) {
            return (T) new MyRecipesViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass);
    }
}
