package com.example.recipebox.domain.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.example.recipebox.domain.model.Ingredient;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.ShoppingList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests for ScaleRecipeUseCase")
public class ScaleRecipeUseCaseTest {

    private ScaleRecipeUseCase useCase;
    private Recipe testRecipe;

    @BeforeEach
    public void setUp() {
        useCase = new ScaleRecipeUseCase();
        
        testRecipe = new Recipe();
        testRecipe.setName("Test Pasta");
        testRecipe.setServings(2);
        
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Pasta", 200, Ingredient.Unit.GRAM));
        ingredients.add(new Ingredient("Tomato", 2, Ingredient.Unit.PIECE));
        testRecipe.setIngredients(ingredients);
        
        List<String> steps = new ArrayList<>();
        steps.add("Boil water");
        testRecipe.setSteps(steps);
    }

    @Test
    @Order(1)
    @DisplayName("Should correctly scale ingredients")
    public void testScalingIngredients() {
        List<Ingredient> scaled = useCase.execute(testRecipe, 4);
        
        assertEquals(2, scaled.size());
        assertEquals(400.0, scaled.get(0).getAmount(), 0.01);
        assertEquals(4.0, scaled.get(1).getAmount(), 0.01);
    }

    @Test
    @Order(2)
    @DisplayName("Should throw exception for invalid servings")
    public void testScalingWithInvalidServings() {
        assertThrows(IllegalArgumentException.class, () -> {
            useCase.execute(testRecipe, 0);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Should correctly generate shopping list")
    public void testGenerateShoppingList() {
        ShoppingList list = useCase.generateShoppingList(testRecipe, 4, "Weekly Shopping");
        
        assertEquals("Weekly Shopping", list.getName());
        assertEquals(2, list.getItems().size());
        assertEquals("400 g Pasta", list.getItems().get(0).getAmount());
    }

    @Test
    @Order(4)
    @DisplayName("Validation should succeed for valid recipe")
    public void testValidationSuccess() {
        ScaleRecipeUseCase.ValidationResult result = useCase.validateForSave(testRecipe);
        assertTrue(result.isValid());
    }

    @Test
    @Order(5)
    @DisplayName("Validation should fail for empty name")
    public void testValidationFailEmptyName() {
        testRecipe.setName("");
        ScaleRecipeUseCase.ValidationResult result = useCase.validateForSave(testRecipe);
        assertFalse(result.isValid());
        assertEquals("Recipe name is required", result.getErrorMessage());
    }

    @Test
    @Order(6)
    @DisplayName("Validation should fail when no ingredients are present")
    public void testValidationFailNoIngredients() {
        testRecipe.setIngredients(new ArrayList<>());
        ScaleRecipeUseCase.ValidationResult result = useCase.validateForSave(testRecipe);
        assertFalse(result.isValid());
        assertEquals("Recipe must contain at least one ingredient", result.getErrorMessage());
    }
}
