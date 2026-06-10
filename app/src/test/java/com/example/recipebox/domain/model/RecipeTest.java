package com.example.recipebox.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Tests for Recipe model")
public class RecipeTest {

    @Test
    @DisplayName("Should scale ingredients within Recipe object")
    public void testScaleIngredients() {
        Recipe recipe = new Recipe();
        recipe.setServings(4);
        
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(new Ingredient("Rice", 200, Ingredient.Unit.GRAM));
        recipe.setIngredients(ingredients);
        
        List<Ingredient> scaled = recipe.scaleIngredients(2);
        
        assertEquals(1, scaled.size());
        assertEquals(100.0, scaled.get(0).getAmount(), 0.001);
    }
}
