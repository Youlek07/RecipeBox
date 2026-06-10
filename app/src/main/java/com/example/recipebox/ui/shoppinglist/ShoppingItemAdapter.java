package com.example.recipebox.ui.shoppinglist;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.recipebox.databinding.ItemShoppingBinding;
import com.example.recipebox.domain.model.ShoppingItem;

public class ShoppingItemAdapter extends ListAdapter<ShoppingItem, ShoppingItemAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onToggle(ShoppingItem item);
        void onDelete(ShoppingItem item);
    }

    private final OnItemClickListener listener;

    public ShoppingItemAdapter(OnItemClickListener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemShoppingBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        h.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemShoppingBinding b;

        ViewHolder(ItemShoppingBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(ShoppingItem item) {
            b.checkboxItem.setOnCheckedChangeListener(null);

            b.checkboxItem.setChecked(item.isChecked());
            b.textItemName.setText(item.getName());
            b.textItemAmount.setText(item.getAmount());

            b.textItemName.setPaintFlags(item.isChecked() ?
                    b.textItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG :
                    b.textItemName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            b.textItemName.setAlpha(item.isChecked() ? 0.4f : 1f);

            b.checkboxItem.setOnCheckedChangeListener((btn, checked) -> {
                if (checked != item.isChecked()) {
                    listener.onToggle(item);
                }
            });

            b.getRoot().setOnClickListener(v -> {
                listener.onDelete(item);
            });
        }


    }

    private static final DiffUtil.ItemCallback<ShoppingItem> DIFF = new DiffUtil.ItemCallback<ShoppingItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull ShoppingItem a, @NonNull ShoppingItem b) {
            return a.getId() == b.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ShoppingItem a, @NonNull ShoppingItem b) {
            return a.isChecked() == b.isChecked() && a.getName().equals(b.getName());
        }
    };
}
