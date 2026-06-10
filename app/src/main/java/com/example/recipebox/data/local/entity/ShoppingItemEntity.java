package com.example.recipebox.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "shopping_items",
        foreignKeys = @ForeignKey(
                entity = ShoppingListEntity.class,
                parentColumns = "id",
                childColumns = "listId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("listId")}
)
public class ShoppingItemEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String name;
    public String amount;
    public boolean checked;
    public long listId;
}
