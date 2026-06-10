package com.example.recipebox.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.recipebox.data.local.entity.ShoppingItemEntity;
import com.example.recipebox.data.local.entity.ShoppingListEntity;
import java.util.List;

@Dao
public interface ShoppingDao {

    @Query("SELECT * FROM shopping_lists ORDER BY createdAt DESC")
    List<ShoppingListEntity> getAllLists();

    @Query("SELECT * FROM shopping_lists WHERE id = :id")
    ShoppingListEntity getListById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertList(ShoppingListEntity list);

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    void deleteList(long id);

    @Query("SELECT * FROM shopping_items WHERE listId = :listId ORDER BY id ASC")
    List<ShoppingItemEntity> getItemsForList(long listId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertItem(ShoppingItemEntity item);

    @Update
    void updateItem(ShoppingItemEntity item);

    @Query("DELETE FROM shopping_items WHERE id = :id")
    void deleteItem(long id);

    @Query("DELETE FROM shopping_items WHERE listId = :listId")
    void deleteAllItemsByListId(long listId);

}
