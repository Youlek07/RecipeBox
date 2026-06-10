package com.example.recipebox.domain.model;

import java.util.Objects;

public class Ingredient {

    public enum Unit {
        GRAM("g"),
        KILOGRAM("kg"),
        MILLILITER("ml"),
        LITER("l"),
        TABLESPOON("tbsp"),
        TEASPOON("tsp"),
        CUP("cup"),
        PIECE("pcs"),
        PINCH("pinch"),
        NONE("");

        private final String symbol;

        Unit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public boolean isWeight() {
            return this == GRAM || this == KILOGRAM;
        }

        public boolean isVolume() {
            return this == MILLILITER || this == LITER || this == TABLESPOON || this == TEASPOON || this == CUP;
        }

        public java.util.List<Unit> getCompatibleUnits() {
            java.util.List<Unit> list = new java.util.ArrayList<>();
            for (Unit u : Unit.values()) {
                if (this.isWeight() && u.isWeight()) list.add(u);
                else if (this.isVolume() && u.isVolume()) list.add(u);
                else if (this == u) list.add(u);
            }
            return list;
        }
    }

    private String name;
    private double amount;
    private Unit unit;

    public Ingredient() {}

    public Ingredient(String name, double amount, Unit unit) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient name cannot be empty");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("Ingredient amount cannot be negative");
        }
        this.name = name.trim();
        this.amount = amount;
        this.unit = unit != null ? unit : Unit.NONE;
    }

    public Ingredient scaledBy(double factor) {
        if (factor <= 0) {
            throw new IllegalArgumentException("Scaling factor must be > 0");
        }
        return new Ingredient(this.name, this.amount * factor, this.unit);
    }

    public Ingredient convertTo(Unit targetUnit) {
        if (this.unit == targetUnit) return new Ingredient(name, amount, unit);

        double converted;
        if (unit == Unit.GRAM && targetUnit == Unit.KILOGRAM) {
            converted = amount / 1000.0;
        } else if (unit == Unit.KILOGRAM && targetUnit == Unit.GRAM) {
            converted = amount * 1000.0;
        } else if (unit == Unit.MILLILITER && targetUnit == Unit.LITER) {
            converted = amount / 1000.0;
        } else if (unit == Unit.LITER && targetUnit == Unit.MILLILITER) {
            converted = amount * 1000.0;
        } else if (unit == Unit.TABLESPOON && targetUnit == Unit.MILLILITER) {
            converted = amount * 15.0;
        } else if (unit == Unit.TEASPOON && targetUnit == Unit.MILLILITER) {
            converted = amount * 5.0;
        } else if (unit == Unit.CUP && targetUnit == Unit.MILLILITER) {
            converted = amount * 250.0;
        } else if (unit == Unit.MILLILITER && targetUnit == Unit.TABLESPOON) {
            converted = amount / 15.0;
        } else if (unit == Unit.MILLILITER && targetUnit == Unit.TEASPOON) {
            converted = amount / 5.0;
        } else if (unit == Unit.MILLILITER && targetUnit == Unit.CUP) {
            converted = amount / 250.0;
        } else {
            throw new UnsupportedOperationException(
                    "Conversion from " + unit.getSymbol() + " to " + targetUnit.getSymbol() + " is not supported");
        }
        return new Ingredient(name, converted, targetUnit);
    }

    public Ingredient getSmartUnit() {
        if (unit == Unit.GRAM && amount >= 1000) return convertTo(Unit.KILOGRAM);
        if (unit == Unit.KILOGRAM && amount < 1 && amount > 0) return convertTo(Unit.GRAM);
        if (unit == Unit.MILLILITER && amount >= 1000) return convertTo(Unit.LITER);
        if (unit == Unit.LITER && amount < 1 && amount > 0) return convertTo(Unit.MILLILITER);
        return new Ingredient(name, amount, unit);
    }

    public String format() {
        String amountStr = (amount == Math.floor(amount))
                ? String.valueOf((int) amount)
                : String.format("%.1f", amount);

        if (unit == Unit.NONE) {
            return amount > 0 ? amountStr + " " + name : name;
        }
        return amountStr + " " + unit.getSymbol() + " " + name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Unit getUnit() {
        return unit;
    }
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient)) return false;
        Ingredient that = (Ingredient) o;
        return Double.compare(that.amount, amount) == 0
                && Objects.equals(name, that.name)
                && unit == that.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, amount, unit);
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                ", unit=" + unit +
                '}';
    }
}
