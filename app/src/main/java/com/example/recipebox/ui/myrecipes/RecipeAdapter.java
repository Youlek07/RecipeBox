package com.example.recipebox.ui.myrecipes;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.recipebox.R;
import com.recipebox.databinding.ItemRecipeBinding;
import com.example.recipebox.domain.model.Recipe;

public class RecipeAdapter extends ListAdapter<Recipe, RecipeAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Recipe recipe);
        void onItemLongClick(Recipe recipe);
    }

    private OnItemClickListener listener;

    public RecipeAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRecipeBinding binding = ItemRecipeBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeBinding binding;

        ViewHolder(ItemRecipeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Recipe recipe) {
            binding.textRecipeName.setText(recipe.getName());
            binding.textCategory.setText(recipe.getCategory());
            binding.textTime.setText(recipe.getTotalTimeMinutes() + " min");
            binding.textServings.setText(binding.getRoot().getContext().getString(R.string.servings_count, recipe.getServings()));
            binding.badgeDifficulty.setText(
                    recipe.getDifficulty() != null ? recipe.getDifficulty().getLabel() : "");

            int bgColor;
            if (recipe.getDifficulty() == Recipe.Difficulty.EASY) {
                bgColor = binding.getRoot().getContext().getColor(R.color.difficulty_easy);
            } else if (recipe.getDifficulty() == Recipe.Difficulty.HARD) {
                bgColor = binding.getRoot().getContext().getColor(R.color.difficulty_hard);
            } else {
                bgColor = binding.getRoot().getContext().getColor(R.color.difficulty_medium);
            }
            binding.badgeDifficulty.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(bgColor));

            if (recipe.getImageUrl() != null && !recipe.getImageUrl().isEmpty()) {
                Glide.with(binding.imageRecipe)
                        .load(recipe.getImageUrl())
                        .centerCrop()
                        .placeholder(R.color.image_placeholder)
                        .into(binding.imageRecipe);
            } else {
                binding.imageRecipe.setImageResource(R.color.image_placeholder);
            }

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(recipe);
            });
            binding.getRoot().setOnLongClickListener(v -> {
                if (listener != null) listener.onItemLongClick(recipe);
                return true;
            });
        }
    }

    private static final DiffUtil.ItemCallback<Recipe> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Recipe>() {
                @Override
                public boolean areItemsTheSame(@NonNull Recipe a, @NonNull Recipe b) {
                    return a.getId() == b.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Recipe a, @NonNull Recipe b) {
                    return a.getName().equals(b.getName())
                            && a.getServings() == b.getServings();
                }
            };
}
