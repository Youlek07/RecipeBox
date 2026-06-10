package com.example.recipebox.ui.myrecipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.recipebox.R;
import com.example.recipebox.data.local.AppDatabase;
import com.example.recipebox.data.repository.RecipeRepositoryImpl;
import com.recipebox.databinding.FragmentMyRecipesBinding;
import com.example.recipebox.domain.model.Recipe;

public class MyRecipesFragment extends Fragment {

    private FragmentMyRecipesBinding binding;
    private MyRecipesViewModel viewModel;
    private RecipeAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMyRecipesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewModel();
        setupRecyclerView();
        setupFab();
        observeViewModel();

        viewModel.loadRecipes();
    }

    private void setupViewModel() {
        AppDatabase db = AppDatabase.getInstance(requireContext());
        RecipeRepositoryImpl repo = new RecipeRepositoryImpl(db.recipeDao());
        MyRecipesViewModelFactory factory = new MyRecipesViewModelFactory(repo);
        viewModel = new ViewModelProvider(this, factory).get(MyRecipesViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new RecipeAdapter();
        binding.recyclerRecipes.setAdapter(adapter);

        adapter.setListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                Bundle args = new Bundle();
                args.putLong("recipeId", recipe.getId());
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_myRecipes_to_detail, args);
            }

            @Override
            public void onItemLongClick(Recipe recipe) {
                showDeleteDialog(recipe);
            }
        });
    }

    private void setupFab() {
        binding.fabAddRecipe.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_myRecipes_to_add));
    }

    private void observeViewModel() {
        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            adapter.submitList(recipes);
            binding.textEmpty.setVisibility(
                    recipes == null || recipes.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) {
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
        });
    }

    private void showDeleteDialog(Recipe recipe) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete '" + recipe.getName() + "'?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete", (dialog, which) -> {
                    viewModel.deleteRecipe(recipe.getId());
                    Snackbar.make(binding.getRoot(), "Recipe deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                viewModel.saveRecipe(recipe);
                            })
                            .show();
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}