package com.example.recipebox.ui.shoppinglist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.snackbar.Snackbar;
import com.example.recipebox.data.local.AppDatabase;
import com.example.recipebox.data.repository.ShoppingListRepositoryImpl;
import com.recipebox.databinding.FragmentShoppingListBinding;
import com.example.recipebox.domain.model.ShoppingItem;
import com.recipebox.R;

public class ShoppingListFragment extends Fragment {

    private FragmentShoppingListBinding binding;
    private ShoppingListViewModel viewModel;
    private ShoppingItemAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentShoppingListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        ShoppingListRepositoryImpl repo = new ShoppingListRepositoryImpl(db.shoppingDao());
        ShoppingListViewModelFactory factory = new ShoppingListViewModelFactory(repo);
        viewModel = new ViewModelProvider(this, factory).get(ShoppingListViewModel.class);

        setupRecyclerView();
        observeViewModel();

        binding.fabDeleteAll.setOnClickListener(v -> showDeleteConfirmationDialog());

        viewModel.loadLists();
    }

    private void showDeleteConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete)
                .setMessage(R.string.confirm_delete_all)
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteAllItems())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    private void setupRecyclerView() {
        adapter = new ShoppingItemAdapter(new ShoppingItemAdapter.OnItemClickListener() {
            @Override
            public void onToggle(ShoppingItem item) {
                viewModel.toggleItem(item);
            }

            @Override
            public void onDelete(ShoppingItem item) {
                showDeleteItemConfirmationDialog(item);
            }
        });

        binding.recyclerItems.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerItems.setAdapter(adapter);
    }

    private void showDeleteItemConfirmationDialog(ShoppingItem item) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.delete)
                .setMessage(getString(R.string.delete) + " " + item.getName() + "?")
                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deleteItem(item))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    private void observeViewModel() {
        viewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            adapter.submitList(items);
            binding.textEmpty.setVisibility(
                    items == null || items.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}