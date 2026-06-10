package com.example.recipebox.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.recipebox.R;
import com.example.recipebox.data.local.AppDatabase;
import com.example.recipebox.data.repository.RecipeRepositoryImpl;
import com.example.recipebox.data.repository.ShoppingListRepositoryImpl;
import com.recipebox.databinding.FragmentRecipeDetailBinding;
import com.example.recipebox.domain.model.Ingredient;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.domain.model.RecipeRepository;
import com.example.recipebox.domain.model.ShoppingList;
import com.example.recipebox.domain.usecase.ScaleRecipeUseCase;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecipeDetailFragment extends Fragment {

    private FragmentRecipeDetailBinding binding;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Recipe recipe;
    private ScaleRecipeUseCase scaleUseCase;
    private int currentServings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scaleUseCase = new ScaleRecipeUseCase();
        RecipeDetailFragmentArgs args = RecipeDetailFragmentArgs.fromBundle(requireArguments());
        long recipeId = args.getRecipeId();
        boolean isLocal = args.getIsLocal();

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(View.GONE);

        if (isLocal) {
            loadLocalRecipe(recipeId);
        } else {
            loadApiRecipe((int) recipeId);
        }
    }

    private void loadLocalRecipe(long id) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            RecipeRepositoryImpl repo = new RecipeRepositoryImpl(db.recipeDao());
            recipe = repo.getLocalRecipeById(id);
            handleLoadResult();
        });
    }

    private void loadApiRecipe(int id) {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        RecipeRepositoryImpl repo = new RecipeRepositoryImpl(db.recipeDao());
        repo.getRecipeDetail(id, new RecipeRepository.RecipeCallback<Recipe>() {
            @Override
            public void onSuccess(Recipe result) {
                recipe = result;
                handleLoadResult();
            }

            @Override
            public void onError(String msg) {
                requireActivity().runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Snackbar.make(binding.getRoot(), getString(R.string.error_prefix) + msg, Snackbar.LENGTH_LONG).show();
                });
            }
        });
    }

    private void handleLoadResult() {
        if (recipe != null) {
            currentServings = recipe.getServings();
            requireActivity().runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.scrollView.setVisibility(View.VISIBLE);
                bindRecipe();
            });
        } else {
            requireActivity().runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), "Nie znaleziono przepisu", Snackbar.LENGTH_LONG).show();
            });
        }
    }

    private void bindRecipe() {
        binding.textDetailName.setText(recipe.getName());
        binding.textDetailCategory.setText(recipe.getCategory());
        binding.textDetailTime.setText(recipe.getTotalTimeMinutes() + " min");
        binding.textDetailDifficulty.setText(
                recipe.getDifficulty() != null ? recipe.getDifficulty().getLabel() : "");
        binding.textDetailDescription.setText(recipe.getDescription());
        binding.textRecipeApiId.setText("ID: " + recipe.getId());

        if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
            Glide.with(this).load(recipe.getImageUrl()).centerCrop()
                    .placeholder(R.color.image_placeholder).into(binding.imageDetail);
        }

        binding.textServingsCount.setText(String.valueOf(currentServings));
        updateIngredients();

        StringBuilder stepsBuilder = new StringBuilder();
        if (recipe.getSteps() != null) {
            for (int i = 0; i < recipe.getSteps().size(); i++) {
                stepsBuilder.append(i + 1).append(". ").append(recipe.getSteps().get(i)).append("\n\n");
            }
        }
        binding.textDetailSteps.setText(stepsBuilder.toString().trim());

        binding.btnServingsPlus.setOnClickListener(v -> {
            currentServings++;
            binding.textServingsCount.setText(String.valueOf(currentServings));
            updateIngredients();
        });
        binding.btnServingsMinus.setOnClickListener(v -> {
            if (currentServings > 1) {
                currentServings--;
                binding.textServingsCount.setText(String.valueOf(currentServings));
                updateIngredients();
            }
        });

        binding.btnAddToShopping.setOnClickListener(v -> {
            ShoppingList list = scaleUseCase.generateShoppingList(
                    recipe, currentServings, recipe.getName() + " x" + currentServings);
            executor.execute(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                ShoppingListRepositoryImpl repo = new ShoppingListRepositoryImpl(db.shoppingDao());
                repo.insertList(list);
                requireActivity().runOnUiThread(() ->
                        Snackbar.make(binding.getRoot(),
                                R.string.shopping_list_created, Snackbar.LENGTH_SHORT).show());
            });
        });
    }

    private void updateIngredients() {
        try {
            List<Ingredient> scaled = scaleUseCase.execute(recipe, currentServings);
            StringBuilder sb = new StringBuilder();
            for (Ingredient ing : scaled) {
                sb.append("• ").append(ing.format()).append("\n");
            }
            binding.textDetailIngredients.setText(sb.toString().trim());
        } catch (Exception e) {
            binding.textDetailIngredients.setText(R.string.no_ingredients);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        executor.shutdown();
    }
}