package com.example.recipebox.ui.apirecipes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.RecipeRepository;
import java.util.List;

public class ApiRecipesViewModel extends ViewModel {

    private final RecipeRepository repository;
    private final MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ApiRecipesViewModel(RecipeRepository repository) {
        this.repository = repository;
    }

    public void search(String query) {
        loading.setValue(true);
        repository.searchRecipes(query, new RecipeRepository.RecipeCallback<List<Recipe>>() {
            @Override public void onSuccess(List<Recipe> result) {
                recipes.postValue(result);
                loading.postValue(false);
            }
            @Override public void onError(String msg) {
                error.postValue(msg);
                loading.postValue(false);
            }
        });
    }

    public LiveData<List<Recipe>> getRecipes() { return recipes; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }
}
