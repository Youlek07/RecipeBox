package com.example.recipebox.ui.apirecipes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.google.android.material.snackbar.Snackbar;
import com.recipebox.R;
import com.example.recipebox.data.local.AppDatabase;
import com.example.recipebox.data.repository.RecipeRepositoryImpl;
import com.recipebox.databinding.FragmentApiRecipesBinding;
import com.example.recipebox.domain.model.Recipe;
import com.example.recipebox.ui.myrecipes.RecipeAdapter;

public class ApiRecipesFragment extends Fragment {

    private FragmentApiRecipesBinding binding;
    private ApiRecipesViewModel viewModel;
    private RecipeAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentApiRecipesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        RecipeRepositoryImpl repo = new RecipeRepositoryImpl(db.recipeDao());
        viewModel = new ViewModelProvider(this,
                new ApiRecipesViewModelFactory(repo)).get(ApiRecipesViewModel.class);

        setupRecyclerView();
        setupSearch();
        observeViewModel();
        viewModel.search("pasta");
    }

    private void setupRecyclerView() {
        adapter = new RecipeAdapter();
        binding.recyclerApiRecipes.setAdapter(adapter);
        adapter.setListener(new RecipeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Recipe recipe) {
                Bundle args = new Bundle();
                args.putLong("recipeId", recipe.getId());
                args.putBoolean("isLocal", false);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_api_to_detail, args);
            }
            @Override public void onItemLongClick(Recipe recipe) {}
        });
    }

    private void setupSearch() {
        binding.btnSearch.setOnClickListener(v -> {
            String q = binding.editSearch.getText().toString().trim();
            if (!q.isEmpty()) viewModel.search(q);
        });
    }

    private void observeViewModel() {
        viewModel.getRecipes().observe(getViewLifecycleOwner(), recipes -> {
            adapter.submitList(recipes);
            binding.textEmpty.setVisibility(
                    recipes == null || recipes.isEmpty() ? View.VISIBLE : View.GONE);
        });
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));
        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView(); binding = null;
    }
}