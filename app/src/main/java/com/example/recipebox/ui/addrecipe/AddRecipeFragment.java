package com.example.recipebox.ui.addrecipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.example.recipebox.data.local.AppDatabase;
import com.example.recipebox.data.repository.RecipeRepositoryImpl;
import com.recipebox.databinding.FragmentAddRecipeBinding;
import com.recipebox.databinding.ItemAddIngredientBinding;
import com.example.recipebox.domain.model.Ingredient;
import com.example.recipebox.domain.model.Recipe;
import com.recipebox.R;
import com.example.recipebox.ui.myrecipes.MyRecipesViewModel;
import com.example.recipebox.ui.myrecipes.MyRecipesViewModelFactory;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AddRecipeFragment extends Fragment {

    private FragmentAddRecipeBinding binding;
    private MyRecipesViewModel viewModel;
    private String selectedImageUri = null;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri.toString();
                    binding.imagePreview.setAlpha(1.0f);
                    binding.imagePreview.setPadding(0, 0, 0, 0);
                    binding.textAddImage.setVisibility(View.GONE);
                    Glide.with(this).load(uri).centerCrop().into(binding.imagePreview);
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        RecipeRepositoryImpl repo = new RecipeRepositoryImpl(db.recipeDao());
        viewModel = new ViewModelProvider(this,
                new MyRecipesViewModelFactory(repo)).get(MyRecipesViewModel.class);

        binding.btnSave.setOnClickListener(v -> saveRecipe());

        viewModel.getSaveSuccess().observe(getViewLifecycleOwner(), ok -> {
            if (Boolean.TRUE.equals(ok)) {
                Navigation.findNavController(requireView()).popBackStack();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });

        binding.btnAddIngredient.setOnClickListener(v -> addIngredientRow());
        addIngredientRow();

        binding.cardSelectImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void addIngredientRow() {
        ItemAddIngredientBinding itemBinding = ItemAddIngredientBinding.inflate(
                getLayoutInflater(), binding.containerIngredients, true);

        itemBinding.btnRemove.setOnClickListener(v -> {
            binding.containerIngredients.removeView(itemBinding.getRoot());
        });
    }

    private void saveRecipe() {
        String name = binding.editName.getText().toString().trim();
        String desc = binding.editDescription.getText().toString().trim();
        String category = binding.editCategory.getText().toString().trim();
        String servingsStr = binding.editServings.getText().toString().trim();
        String prepStr = binding.editPrepTime.getText().toString().trim();
        String cookStr = binding.editCookTime.getText().toString().trim();
        String stepsText = binding.editSteps.getText().toString().trim();

        int servings = servingsStr.isEmpty() ? 0 : Integer.parseInt(servingsStr);
        int prep = prepStr.isEmpty() ? 0 : Integer.parseInt(prepStr);
        int cook = cookStr.isEmpty() ? 0 : Integer.parseInt(cookStr);

        java.util.List<Ingredient> ingredients = new java.util.ArrayList<>();
        for (int i = 0; i < binding.containerIngredients.getChildCount(); i++) {
            View view = binding.containerIngredients.getChildAt(i);
            ItemAddIngredientBinding itemBinding = ItemAddIngredientBinding.bind(view);

            String ingName = itemBinding.editName.getText().toString().trim();
            String amountStr = itemBinding.editAmount.getText().toString().trim();
            int unitPos = itemBinding.spinnerUnit.getSelectedItemPosition();

            if (!ingName.isEmpty()) {
                double amount = amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr);
                Ingredient.Unit unit = Ingredient.Unit.values()[unitPos];
                ingredients.add(new Ingredient(ingName, amount, unit));
            }
        }

        java.util.List<String> steps = new java.util.ArrayList<>();
        if (!stepsText.isEmpty()) {
            for (String line : stepsText.split("\n")) {
                if (!line.trim().isEmpty()) steps.add(line.trim());
            }
        }

        Recipe.Difficulty difficulty;
        switch (binding.spinnerDifficulty.getSelectedItemPosition()) {
            case 1: difficulty = Recipe.Difficulty.MEDIUM;
            break;
            case 2: difficulty = Recipe.Difficulty.HARD;
            break;
            default: difficulty = Recipe.Difficulty.EASY;
            break;
        }

        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setDescription(desc);
        recipe.setImageUrl(selectedImageUri);
        recipe.setCategory(category.isEmpty() ? getString(R.string.category_other) : category);
        recipe.setServings(servings);
        recipe.setPrepTimeMinutes(prep);
        recipe.setCookTimeMinutes(cook);
        recipe.setDifficulty(difficulty);
        recipe.setIngredients(ingredients);
        recipe.setSteps(steps);
        recipe.setLocal(true);

        viewModel.saveRecipe(recipe);
    }

    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}