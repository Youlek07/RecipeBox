package com.example.recipebox.data.repository;

import com.google.gson.annotations.SerializedName;
import com.example.recipebox.domain.model.Ingredient;
import com.example.recipebox.domain.model.Recipe;
import com.recipebox.BuildConfig;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

//https://api.spoonacular.com/recipes/716429/information?apiKey=KLUCZ_API

interface SpoonacularApi {

    String BASE_URL = "https://api.spoonacular.com/";

    String API_KEY = BuildConfig.KLUCZ_API;

    @GET("recipes/complexSearch")
    Call<SearchResponse> searchRecipes(
            @Query("query") String query,
            @Query("number") int number,
            @Query("addRecipeInformation") boolean addInfo,
            @Query("apiKey") String apiKey
    );

    @GET("recipes/{id}/information")
    Call<RecipeDetailResponse> getRecipeDetail(
            @Path("id") int id,
            @Query("apiKey") String apiKey
    );
}


class SearchResponse {
    @SerializedName("results")
    List<RecipeDetailResponse> results;
}

class RecipeDetailResponse {
    @SerializedName("id") int id;
    @SerializedName("title") String title;
    @SerializedName("summary")String summary;
    @SerializedName("image") String image;
    @SerializedName("servings") int servings;
    @SerializedName("readyInMinutes") int readyInMinutes;
    @SerializedName("preparationMinutes") int prepMinutes;
    @SerializedName("cookingMinutes") int cookMinutes;
    @SerializedName("dishTypes") List<String> dishTypes;
    @SerializedName("extendedIngredients") List<ExtendedIngredient> extendedIngredients;
    @SerializedName("analyzedInstructions") List<AnalyzedInstruction> analyzedInstructions;

    Recipe toDomain() {
        Recipe r = new Recipe();
        r.setId(id);
        r.setName(title);
        r.setDescription(summary != null ? summary.replaceAll("<[^>]+>", "") : "");
        r.setImageUrl(image);
        r.setServings(servings > 0 ? servings : 4);
        r.setPrepTimeMinutes(prepMinutes > 0 ? prepMinutes : readyInMinutes / 2);
        r.setCookTimeMinutes(cookMinutes > 0 ? cookMinutes : readyInMinutes / 2);
        r.setDifficulty(Recipe.Difficulty.MEDIUM);
        r.setCategory(dishTypes != null && !dishTypes.isEmpty() ? dishTypes.get(0) : "Main course");
        r.setLocal(false);

        List<Ingredient> ings = new ArrayList<>();
        if (extendedIngredients != null) {
            for (ExtendedIngredient ei : extendedIngredients) {
                ings.add(new Ingredient(ei.name, ei.amount, mapUnit(ei.unit)));
            }
        }
        r.setIngredients(ings);

        List<String> steps = new ArrayList<>();
        if (analyzedInstructions != null) {
            for (AnalyzedInstruction ai : analyzedInstructions) {
                if (ai.steps != null) {
                    for (InstructionStep s : ai.steps) steps.add(s.step);
                }
            }
        }
        r.setSteps(steps);
        return r;
    }

    private Ingredient.Unit mapUnit(String unit) {
        if (unit == null || unit.isEmpty()) return Ingredient.Unit.NONE;
        String u = unit.toLowerCase();
        if (u.contains("gram") || u.equals("g")) return Ingredient.Unit.GRAM;
        if (u.contains("kilogram") || u.equals("kg")) return Ingredient.Unit.KILOGRAM;
        if (u.contains("milliliter") || u.equals("ml")) return Ingredient.Unit.MILLILITER;
        if (u.contains("liter") || u.equals("l")) return Ingredient.Unit.LITER;
        if (u.contains("spoon") || u.equals("tbsp")) return Ingredient.Unit.TABLESPOON;
        if (u.contains("teaspoon") || u.equals("tsp")) return Ingredient.Unit.TEASPOON;
        if (u.contains("cup")) return Ingredient.Unit.CUP;
        if (u.contains("pinch")) return Ingredient.Unit.PINCH;
        if (u.contains("piece") || u.contains("serving")) return Ingredient.Unit.PIECE;
        return Ingredient.Unit.NONE;
    }
}

class ExtendedIngredient {
    @SerializedName("name") String name;
    @SerializedName("amount") double amount;
    @SerializedName("unit") String unit;
}

class AnalyzedInstruction {
    @SerializedName("steps") List<InstructionStep> steps;
}

class InstructionStep {
    @SerializedName("number") int number;
    @SerializedName("step") String step;
}
