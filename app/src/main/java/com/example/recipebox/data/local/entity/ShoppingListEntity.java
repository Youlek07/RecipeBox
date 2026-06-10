package com.example.recipebox.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_lists")
public class ShoppingListEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public long createdAt;
}
