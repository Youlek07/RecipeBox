package com.example.recipebox.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.recipebox.R;
import com.example.recipebox.data.local.AppDatabase;
import com.example.recipebox.data.repository.RecipeRepositoryImpl;
import com.recipebox.databinding.FragmentHomeBinding;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        RecipeRepositoryImpl repo = new RecipeRepositoryImpl(db.recipeDao());
        HomeViewModelFactory factory = new HomeViewModelFactory(repo);
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        observeViewModel();
        viewModel.load();
    }

    private void observeViewModel() {
        viewModel.getRecipeOfDay().observe(getViewLifecycleOwner(), recipe -> {
            if (recipe != null) {
                binding.textRecipeOfDayTitle.setText(recipe.getName());
                binding.textRecipeOfDayMeta.setText(
                        recipe.getCategory() + " · " + recipe.getTotalTimeMinutes() + " min · "
                                + (recipe.getDifficulty() != null ? recipe.getDifficulty().getLabel() : ""));
                if (recipe.getImageUrl() != null) {
                    Glide.with(binding.imageRecipeOfDay)
                            .load(recipe.getImageUrl())
                            .centerCrop()
                            .placeholder(R.color.image_placeholder)
                            .into(binding.imageRecipeOfDay);
                }
                binding.cardRecipeOfDay.setVisibility(View.VISIBLE);
                binding.textNoRecipeOfDay.setVisibility(View.GONE);

                binding.cardRecipeOfDay.setOnClickListener(v -> {
                    Bundle args = new Bundle();
                    args.putLong("recipeId", recipe.getId());
                    args.putBoolean("isLocal", recipe.isLocal());
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_home_to_detail, args);
                });
            } else {
                binding.cardRecipeOfDay.setVisibility(View.GONE);
                binding.textNoRecipeOfDay.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getTotalRecipes().observe(getViewLifecycleOwner(), count ->
                binding.textStatRecipes.setText(String.valueOf(count)));

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());
        binding.textTodayDate.setText(sdf.format(new Date()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}