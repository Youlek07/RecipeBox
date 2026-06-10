package com.example.recipebox.ui.shoppinglist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.recipebox.domain.model.ShoppingItem;
import com.example.recipebox.domain.model.ShoppingList;
import com.example.recipebox.domain.model.ShoppingListRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShoppingListViewModel extends ViewModel {

    private final ShoppingListRepository repository;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<List<ShoppingItem>> items = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ShoppingListViewModel(ShoppingListRepository repository) {
        this.repository = repository;
    }

    public void loadLists() {
        executor.execute(() -> {
            try {
                List<ShoppingList> allLists = repository.getAllLists();
                List<ShoppingItem> allItems = new ArrayList<>();
                for (ShoppingList list : allLists) {
                    allItems.addAll(list.getItems());
                }
                items.postValue(allItems);
            } catch (Exception e) {
                error.postValue("Loading error: " + e.getMessage());
            }
        });
    }

    public void toggleItem(ShoppingItem item) {
        executor.execute(() -> {
            try {
                item.setChecked(!item.isChecked());
                repository.updateItem(item);
                loadLists();
            } catch (Exception e) {
                error.postValue("Update error: " + e.getMessage());
            }
        });
    }

    public LiveData<List<ShoppingItem>> getItems() { return items; }
    public LiveData<String> getError() { return error; }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }

    public void deleteItem(ShoppingItem item) {
        executor.execute(() -> {
            try {
                repository.deleteItem(item);
                loadLists();
            } catch (Exception e) {
                error.postValue("Delete error: " + e.getMessage());
            }
        });
    }

    public void deleteAllItems() {
        executor.execute(() -> {
            try {
                List<ShoppingList> allLists = repository.getAllLists();
                for (ShoppingList list : allLists) {
                    repository.deleteList(list.getId());
                }
                loadLists();
            } catch (Exception e) {
                error.postValue("Error while deleting list: " + e.getMessage());
            }
        });
    }


}
