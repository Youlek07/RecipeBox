package com.example.recipebox.ui.home;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import com.example.recipebox.InstantExecutorExtension;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;

@ExtendWith({MockitoExtension.class, InstantExecutorExtension.class})
@DisplayName("Tests for HomeViewModel")
public class HomeViewModelTest {

    @Mock
    private RecipeRepository repository;

    private HomeViewModel viewModel;

    @BeforeEach
    public void setup() {
        viewModel = new HomeViewModel(repository);
    }

    @Test
    @DisplayName("Should update total recipes count after loading")
    public void load_updatesTotalRecipesCount() throws InterruptedException {
        List<Recipe> localRecipes = new ArrayList<>();
        localRecipes.add(new Recipe());
        localRecipes.add(new Recipe());
        when(repository.getAllLocalRecipes()).thenReturn(localRecipes);

        viewModel.load();
        Thread.sleep(100);

        assertEquals(2, viewModel.getTotalRecipes().getValue());
    }

    @Test
    @DisplayName("Should fetch recipe from API when load is called")
    public void load_fetchesRecipeFromApi_whenCalled() throws InterruptedException {
        List<Recipe> apiRecipes = new ArrayList<>();
        Recipe mockRecipe = new Recipe();
        mockRecipe.setName("Test API Recipe");
        apiRecipes.add(mockRecipe);

        doAnswer(invocation -> {
            RecipeRepository.RecipeCallback<List<Recipe>> callback = invocation.getArgument(1);
            callback.onSuccess(apiRecipes);
            return null;
        }).when(repository).searchRecipes(anyString(), any());

        viewModel.load();
        Thread.sleep(100);

        verify(repository).searchRecipes(anyString(), any());
        assertEquals("Test API Recipe", viewModel.getRecipeOfDay().getValue().getName());
    }
}
