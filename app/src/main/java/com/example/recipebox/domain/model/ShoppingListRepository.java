package com.example.recipebox.domain.model;

import java.util.List;

public interface ShoppingListRepository {
    List<ShoppingList> getAllLists();
    ShoppingList getListById(long id);
    long insertList(ShoppingList list);
    void updateItem(ShoppingItem item);
    void deleteList(long id);
    long insertItem(ShoppingItem item);
    public void deleteItem(ShoppingItem item);
    void deleteAllItemsByListId(long listId);

}
