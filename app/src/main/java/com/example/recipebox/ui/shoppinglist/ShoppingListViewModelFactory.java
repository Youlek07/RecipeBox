package com.example.recipebox.ui.shoppinglist;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.recipebox.domain.model.ShoppingListRepository;

public class ShoppingListViewModelFactory implements ViewModelProvider.Factory {
    private final ShoppingListRepository repository;

    public ShoppingListViewModelFactory(ShoppingListRepository repository) {
        this.repository = repository;
    }

    @NonNull @Override @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ShoppingListViewModel(repository);
    }
}
