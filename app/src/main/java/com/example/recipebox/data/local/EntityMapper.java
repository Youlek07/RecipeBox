package com.example.recipebox.data.local;

import com.example.recipebox.data.local.entity.IngredientEntity;
import com.example.recipebox.data.local.entity.RecipeEntity;
import com.example.recipebox.data.local.entity.ShoppingItemEntity;
import com.example.recipebox.data.local.entity.ShoppingListEntity;
import com.example.recipebox.domain.model.Ingredient;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.ShoppingItem;
import com.example.recipebox.domain.model.ShoppingList;
import java.util.ArrayList;
import java.util.List;

public class EntityMapper {


    public static Recipe toDomain(RecipeEntity e) {
        if (e == null) return null;
        Recipe r = new Recipe();
        r.setId(e.id);
        r.setName(e.name);
        r.setDescription(e.description);
        r.setImageUrl(e.imageUrl);
        r.setServings(e.servings);
        r.setPrepTimeMinutes(e.prepTimeMinutes);
        r.setCookTimeMinutes(e.cookTimeMinutes);
        r.setDifficulty(parseDifficulty(e.difficulty));
        r.setCategory(e.category);
        r.setLocal(e.isLocal);
        r.setIngredients(toIngredientDomainList(e.ingredients));
        r.setSteps(e.steps);
        return r;
    }

    public static RecipeEntity toEntity(Recipe r) {
        if (r == null) return null;
        RecipeEntity e = new RecipeEntity();
        e.id = r.getId();
        e.name = r.getName();
        e.description = r.getDescription();
        e.imageUrl = r.getImageUrl();
        e.servings = r.getServings();
        e.prepTimeMinutes = r.getPrepTimeMinutes();
        e.cookTimeMinutes = r.getCookTimeMinutes();
        e.difficulty = r.getDifficulty() != null ? r.getDifficulty().name() : "EASY";
        e.category = r.getCategory();
        e.isLocal = r.isLocal();
        e.ingredients = toIngredientEntityList(r.getIngredients());
        e.steps = r.getSteps();
        return e;
    }

    public static List<Recipe> toDomainRecipeList(List<RecipeEntity> entities) {
        List<Recipe> list = new ArrayList<>();
        if (entities == null) return list;
        for (RecipeEntity e : entities) list.add(toDomain(e));
        return list;
    }


    public static Ingredient toIngredientDomain(IngredientEntity e) {
        if (e == null) return null;
        Ingredient.Unit unit = parseUnit(e.unit);
        return new Ingredient(e.name, e.amount, unit);
    }

    public static IngredientEntity toIngredientEntity(Ingredient i) {
        if (i == null) return null;
        String symbol = i.getUnit() != null ? i.getUnit().getSymbol() : "";
        return new IngredientEntity(i.getName(), i.getAmount(), symbol);
    }

    public static List<Ingredient> toIngredientDomainList(List<IngredientEntity> entities) {
        List<Ingredient> list = new ArrayList<>();
        if (entities == null) return list;
        for (IngredientEntity e : entities) list.add(toIngredientDomain(e));
        return list;
    }

    public static List<IngredientEntity> toIngredientEntityList(List<Ingredient> domain) {
        List<IngredientEntity> list = new ArrayList<>();
        if (domain == null) return list;
        for (Ingredient i : domain) list.add(toIngredientEntity(i));
        return list;
    }


    public static ShoppingList toShoppingListDomain(ShoppingListEntity e, List<ShoppingItemEntity> items) {
        ShoppingList list = new ShoppingList();
        list.setId(e.id);
        list.setName(e.name);
        list.setCreatedAt(e.createdAt);
        List<ShoppingItem> domainItems = new ArrayList<>();
        if (items != null) {
            for (ShoppingItemEntity i : items) domainItems.add(toShoppingItemDomain(i));
        }
        list.setItems(domainItems);
        return list;
    }

    public static ShoppingListEntity toShoppingListEntity(ShoppingList list) {
        ShoppingListEntity e = new ShoppingListEntity();
        e.id = list.getId();
        e.name = list.getName();
        e.createdAt = list.getCreatedAt();
        return e;
    }

    public static ShoppingItem toShoppingItemDomain(ShoppingItemEntity e) {
        return new ShoppingItem(e.id, e.name, e.amount, e.checked, e.listId);
    }

    public static ShoppingItemEntity toShoppingItemEntity(ShoppingItem i) {
        ShoppingItemEntity e = new ShoppingItemEntity();
        e.id = i.getId();
        e.name = i.getName();
        e.amount = i.getAmount();
        e.checked = i.isChecked();
        e.listId = i.getListId();
        return e;
    }


    private static Recipe.Difficulty parseDifficulty(String s) {
        try {
            return Recipe.Difficulty.valueOf(s);
        } catch (Exception e) {
            return Recipe.Difficulty.EASY;
        }
    }

    private static Ingredient.Unit parseUnit(String symbol) {
        if (symbol == null) return Ingredient.Unit.NONE;
        for (Ingredient.Unit u : Ingredient.Unit.values()) {
            if (u.getSymbol().equalsIgnoreCase(symbol)) return u;
        }
        return Ingredient.Unit.NONE;
    }
}
