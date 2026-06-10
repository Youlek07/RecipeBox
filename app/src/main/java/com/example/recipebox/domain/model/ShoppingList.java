package com.example.recipebox.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShoppingList {

    private long id;
    private String name;
    private List<ShoppingItem> items;
    private long createdAt;

    public ShoppingList() {
        this.items = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }

    public ShoppingList(long id, String name, List<ShoppingItem> items) {
        this.id = id;
        this.name = name;
        this.items = items != null ? items : new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
    }

    public int getCheckedCount() {
        int count = 0;
        for (ShoppingItem item : items) {
            if (item.isChecked()) count++;
        }
        return count;
    }

    public int getTotalCount() {
        return items.size();
    }

    public boolean isCompleted() {
        return !items.isEmpty() && getCheckedCount() == getTotalCount();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ShoppingItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingItem> items) {
        this.items = items;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShoppingList)) return false;
        ShoppingList that = (ShoppingList) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
