package com.example.recipebox.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.RecipeRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeViewModel extends ViewModel {

    private final RecipeRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Recipe> recipeOfDay = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalRecipes = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> totalLists = new MutableLiveData<>(0);

    public HomeViewModel(RecipeRepository repository) {
        this.repository = repository;
    }

    public void load() {
        executor.execute(() -> {
            try {
                List<Recipe> localRecipes = repository.getAllLocalRecipes();
                totalRecipes.postValue(localRecipes.size());
                
                String[] queries = {"popular", "chicken", "pasta", "salad", "pizza", "dessert", "healthy", "quick", "dinner", "breakfast"};
                String randomQuery = queries[new Random().nextInt(queries.length)];

                repository.searchRecipes(randomQuery, new RecipeRepository.RecipeCallback<List<Recipe>>() {
                    @Override
                    public void onSuccess(List<Recipe> result) {
                        if (result != null && !result.isEmpty()) {
                            int idx = new Random().nextInt(result.size());
                            recipeOfDay.postValue(result.get(idx));
                        } else if (!localRecipes.isEmpty()) {
                            int idx = new Random().nextInt(localRecipes.size());
                            recipeOfDay.postValue(localRecipes.get(idx));
                        }
                    }
                    @Override public void onError(String msg) {
                        if (!localRecipes.isEmpty()) {
                            int idx = new Random().nextInt(localRecipes.size());
                            recipeOfDay.postValue(localRecipes.get(idx));
                        }
                    }
                });
            } catch (Exception ignored) {}
        });
    }

    public LiveData<Recipe> getRecipeOfDay() { return recipeOfDay; }
    public LiveData<Integer> getTotalRecipes() { return totalRecipes; }
    public LiveData<Integer> getTotalLists() { return totalLists; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}