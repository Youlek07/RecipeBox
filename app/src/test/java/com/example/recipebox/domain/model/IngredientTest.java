package com.example.recipebox.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for Ingredient model")
public class IngredientTest {

    @Test
    @DisplayName("Should correctly convert Gram to Kilogram")
    public void testConversionGramToKilogram() {
        Ingredient ing = new Ingredient("Flour", 1500, Ingredient.Unit.GRAM);
        Ingredient converted = ing.convertTo(Ingredient.Unit.KILOGRAM);
        
        assertEquals(1.5, converted.getAmount(), 0.001);
        assertEquals(Ingredient.Unit.KILOGRAM, converted.getUnit());
    }

    @Test
    @DisplayName("Should correctly convert Milliliter to Liter")
    public void testConversionMilliliterToLiter() {
        Ingredient ing = new Ingredient("Milk", 500, Ingredient.Unit.MILLILITER);
        Ingredient converted = ing.convertTo(Ingredient.Unit.LITER);
        
        assertEquals(0.5, converted.getAmount(), 0.001);
    }

    @Test
    @DisplayName("Should automatically select smart unit (g -> kg)")
    public void testSmartUnit() {
        Ingredient ing = new Ingredient("Sugar", 1200, Ingredient.Unit.GRAM);
        Ingredient smart = ing.getSmartUnit();
        
        assertEquals(Ingredient.Unit.KILOGRAM, smart.getUnit());
        assertEquals(1.2, smart.getAmount(), 0.001);
    }

    @Test
    @DisplayName("Should correctly format ingredient text")
    public void testFormat() {
        Ingredient ing = new Ingredient("Salt", 5, Ingredient.Unit.GRAM);
        assertEquals("5 g Salt", ing.format());
        
        Ingredient ing2 = new Ingredient("Apple", 2, Ingredient.Unit.PIECE);
        assertEquals("2 pcs Apple", ing2.format());
    }
}
