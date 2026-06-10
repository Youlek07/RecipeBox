package com.example.recipebox.ui.apirecipes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
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
@DisplayName("Tests for ApiRecipesViewModel")
public class ApiRecipesViewModelTest {

    @Mock
    private RecipeRepository repository;

    private ApiRecipesViewModel viewModel;

    @BeforeEach
    public void setup() {
        viewModel = new ApiRecipesViewModel(repository);
    }

    @Test
    @DisplayName("Should update recipes list on success")
    public void search_updatesRecipes_onSuccess() {
        String query = "pizza";
        List<Recipe> mockRecipes = new ArrayList<>();
        mockRecipes.add(new Recipe());
        
        doAnswer(invocation -> {
            RecipeRepository.RecipeCallback<List<Recipe>> callback = invocation.getArgument(1);
            callback.onSuccess(mockRecipes);
            return null;
        }).when(repository).searchRecipes(eq(query), any());

        viewModel.search(query);

        verify(repository).searchRecipes(eq(query), any());
        assertEquals(mockRecipes, viewModel.getRecipes().getValue());
        assertFalse(viewModel.getLoading().getValue());
    }

    @Test
    @DisplayName("Should update error message on API error")
    public void search_updatesError_onError() {
        String query = "error";
        String errorMessage = "Network Error";
        
        doAnswer(invocation -> {
            RecipeRepository.RecipeCallback<List<Recipe>> callback = invocation.getArgument(1);
            callback.onError(errorMessage);
            return null;
        }).when(repository).searchRecipes(eq(query), any());

        viewModel.search(query);

        assertEquals(errorMessage, viewModel.getError().getValue());
        assertFalse(viewModel.getLoading().getValue());
    }
}
