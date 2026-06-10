package com.example.recipebox.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.example.recipebox.data.local.dao.RecipeDao;
import com.example.recipebox.data.local.dao.ShoppingDao;
import com.example.recipebox.data.local.entity.RecipeEntity;
import com.example.recipebox.data.local.entity.ShoppingItemEntity;
import com.example.recipebox.data.local.entity.ShoppingListEntity;

@Database(
        entities = {RecipeEntity.class, ShoppingListEntity.class, ShoppingItemEntity.class},
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters.class)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract RecipeDao recipeDao();
    public abstract ShoppingDao shoppingDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "recipebox.db"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
