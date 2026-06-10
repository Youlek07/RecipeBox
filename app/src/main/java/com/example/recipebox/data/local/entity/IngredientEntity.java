package com.example.recipebox.data.local.entity;

public class IngredientEntity {
    public String name;
    public double amount;
    public String unit;

    public IngredientEntity() {}

    public IngredientEntity(String name, double amount, String unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
    }
}
