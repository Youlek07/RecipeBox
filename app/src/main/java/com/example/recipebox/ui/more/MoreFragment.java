package com.example.recipebox.ui.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.example.recipebox.domain.model.Ingredient;
import com.recipebox.R;
import com.recipebox.databinding.DialogUnitConverterBinding;
import com.recipebox.databinding.FragmentMoreBinding;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

public class MoreFragment extends Fragment {

    private FragmentMoreBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean isDark = (AppCompatDelegate.getDefaultNightMode()
                == AppCompatDelegate.MODE_NIGHT_YES);
        binding.switchDarkMode.setChecked(isDark);

        binding.switchDarkMode.setOnCheckedChangeListener((btn, checked) -> {
            AppCompatDelegate.setDefaultNightMode(
                    checked ? AppCompatDelegate.MODE_NIGHT_YES
                            : AppCompatDelegate.MODE_NIGHT_NO);
        });

        binding.rowConverter.setOnClickListener(v -> showConverterDialog());

        binding.rowSettings.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.settings)
                        .setMessage(R.string.settings_empty)
                        .setPositiveButton(R.string.ok, null)
                        .show());

        binding.rowAbout.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.about_author)
                        .setMessage(R.string.author_info)
                        .setPositiveButton(R.string.ok, null)
                        .show());
    }

    private void showConverterDialog() {
        DialogUnitConverterBinding dialogBinding = DialogUnitConverterBinding.inflate(getLayoutInflater());

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { updateResult(dialogBinding); }
            @Override public void afterTextChanged(Editable s) {}
        };

        dialogBinding.editValueFrom.addTextChangedListener(watcher);

        dialogBinding.spinnerUnitFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Ingredient.Unit selectedFrom = Ingredient.Unit.values()[position];
                java.util.List<Ingredient.Unit> compatibles = selectedFrom.getCompatibleUnits();

                java.util.List<String> entries = new java.util.ArrayList<>();
                for (Ingredient.Unit u : compatibles) {
                    entries.add(u.getSymbol().isEmpty() ? "-" : u.getSymbol());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, entries);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                dialogBinding.spinnerUnitTo.setAdapter(adapter);

                updateResult(dialogBinding);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        dialogBinding.spinnerUnitTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateResult(dialogBinding);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setPositiveButton(R.string.close, null)
                .show();
    }

    private void updateResult(DialogUnitConverterBinding b) {
        String valStr = b.editValueFrom.getText().toString();
        if (valStr.isEmpty()) {
            b.textResultValue.setText("0");
            return;
        }

        try {
            double val = Double.parseDouble(valStr);
            int fromPos = b.spinnerUnitFrom.getSelectedItemPosition();
            Ingredient.Unit fromUnit = Ingredient.Unit.values()[fromPos];

            if (b.spinnerUnitTo.getSelectedItem() == null) return;

            String selectedToSymbol = b.spinnerUnitTo.getSelectedItem().toString();
            Ingredient.Unit toUnit = Ingredient.Unit.NONE;
            for (Ingredient.Unit u : Ingredient.Unit.values()) {
                String sym = u.getSymbol().isEmpty() ? "-" : u.getSymbol();
                if (sym.equals(selectedToSymbol)) {
                    toUnit = u;
                    break;
                }
            }

            Ingredient dummy = new Ingredient("dummy", val, fromUnit);
            Ingredient result = dummy.convertTo(toUnit);

            String resultStr = (result.getAmount() == Math.floor(result.getAmount()))
                    ? String.valueOf((int) result.getAmount())
                    : String.format("%.2f", result.getAmount());

            b.textResultValue.setText(resultStr);
        } catch (UnsupportedOperationException e) {
            b.textResultValue.setText("---");
        } catch (Exception e) {
            b.textResultValue.setText("?");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}