package com.example.recipebox.ui.detail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import com.example.recipebox.InstantExecutorExtension;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.RecipeRepository;
import com.example.recipebox.domain.model.ShoppingListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class, InstantExecutorExtension.class})
@DisplayName("Tests for RecipeDetailViewModel")
public class RecipeDetailViewModelTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private ShoppingListRepository shoppingRepository;

    private RecipeDetailViewModel viewModel;

    @BeforeEach
    public void setup() {
        viewModel = new RecipeDetailViewModel(recipeRepository, shoppingRepository);
    }

    @Test
    @DisplayName("Should update recipe LiveData when loading succeeds")
    public void loadRecipe_updatesRecipeLiveData() throws InterruptedException {
        long recipeId = 1L;
        Recipe mockRecipe = new Recipe();
        mockRecipe.setId(recipeId);
        mockRecipe.setName("Test Recipe");
        mockRecipe.setServings(4);

        when(recipeRepository.getLocalRecipeById(recipeId)).thenReturn(mockRecipe);

        viewModel.loadRecipe(recipeId);

        Thread.sleep(100);

        assertEquals("Test Recipe", viewModel.getRecipe().getValue().getName());
        assertEquals(4, viewModel.getCurrentServings().getValue());
    }
}
