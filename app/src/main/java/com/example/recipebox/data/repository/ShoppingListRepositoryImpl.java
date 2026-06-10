package com.example.recipebox.data.repository;

import com.example.recipebox.data.local.EntityMapper;
import com.example.recipebox.data.local.dao.ShoppingDao;
import com.example.recipebox.data.local.entity.ShoppingItemEntity;
import com.example.recipebox.data.local.entity.ShoppingListEntity;
import com.example.recipebox.domain.model.ShoppingItem;
import com.example.recipebox.domain.model.ShoppingList;
import com.example.recipebox.domain.model.ShoppingListRepository;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListRepositoryImpl implements ShoppingListRepository {

    private final ShoppingDao dao;

    public ShoppingListRepositoryImpl(ShoppingDao dao) {
        this.dao = dao;
    }

    @Override
    public List<ShoppingList> getAllLists() {
        List<ShoppingListEntity> entities = dao.getAllLists();
        List<ShoppingList> result = new ArrayList<>();
        for (ShoppingListEntity e : entities) {
            List<ShoppingItemEntity> items = dao.getItemsForList(e.id);
            result.add(EntityMapper.toShoppingListDomain(e, items));
        }
        return result;
    }

    @Override
    public ShoppingList getListById(long id) {
        ShoppingListEntity e = dao.getListById(id);
        if (e == null) return null;
        List<ShoppingItemEntity> items = dao.getItemsForList(id);
        return EntityMapper.toShoppingListDomain(e, items);
    }

    @Override
    public long insertList(ShoppingList list) {
        ShoppingListEntity entity = EntityMapper.toShoppingListEntity(list);
        long listId = dao.insertList(entity);
        if (list.getItems() != null) {
            for (ShoppingItem item : list.getItems()) {
                item.setListId(listId);
                dao.insertItem(EntityMapper.toShoppingItemEntity(item));
            }
        }
        return listId;
    }

    @Override
    public void updateItem(ShoppingItem item) {
        dao.updateItem(EntityMapper.toShoppingItemEntity(item));
    }

    @Override
    public void deleteList(long id) {
        dao.deleteList(id);
    }

    @Override
    public long insertItem(ShoppingItem item) {
        return dao.insertItem(EntityMapper.toShoppingItemEntity(item));
    }

    @Override
    public void deleteItem(ShoppingItem item) {
        dao.deleteItem(item.getId());
    }

    @Override
    public void deleteAllItemsByListId(long listId) {
        dao.deleteAllItemsByListId(listId);
    }
}