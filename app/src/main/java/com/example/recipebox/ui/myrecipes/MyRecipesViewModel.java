package com.example.recipebox.ui.myrecipes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.RecipeRepository;
import com.example.recipebox.domain.usecase.DeleteRecipeUseCase;
import com.example.recipebox.domain.usecase.ScaleRecipeUseCase;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyRecipesViewModel extends ViewModel {

    private final RecipeRepository repository;
    private final ScaleRecipeUseCase scaleRecipeUseCase;
    private final DeleteRecipeUseCase deleteRecipeUseCase;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();

    public MyRecipesViewModel(RecipeRepository repository) {
        this.repository = repository;
        this.scaleRecipeUseCase = new ScaleRecipeUseCase();
        this.deleteRecipeUseCase = new DeleteRecipeUseCase(repository);
    }


    public void loadRecipes() {
        loading.setValue(true);
        executor.execute(() -> {
            try {
                List<Recipe> list = repository.getAllLocalRecipes();
                recipes.postValue(list);
            } catch (Exception e) {
                error.postValue("Error loading recipes: " + e.getMessage());
            } finally {
                loading.postValue(false);
            }
        });
    }


    public void saveRecipe(Recipe recipe) {
        ScaleRecipeUseCase.ValidationResult validation =
                scaleRecipeUseCase.validateForSave(recipe);

        if (!validation.isValid()) {
            error.setValue(validation.getErrorMessage());
            return;
        }

        executor.execute(() -> {
            try {
                repository.insertRecipe(recipe);
                saveSuccess.postValue(true);
                loadRecipesInternal();
            } catch (Exception e) {
                error.postValue("Save error: " + e.getMessage());
            }
        });
    }


    public void deleteRecipe(long id) {
        executor.execute(() -> {
            try {
                deleteRecipeUseCase.execute(id);
                loadRecipesInternal();
            } catch (Exception e) {
                error.postValue("Delete error: " + e.getMessage());
            }
        });
    }


    private void loadRecipesInternal() {
        try {
            List<Recipe> list = repository.getAllLocalRecipes();
            recipes.postValue(list);
        } catch (Exception e) {
            error.postValue("Loading error: " + e.getMessage());
        }
    }


    public LiveData<List<Recipe>> getRecipes() { return recipes; }
    public LiveData<String> getError() { return error; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<Boolean> getSaveSuccess() { return saveSuccess; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}