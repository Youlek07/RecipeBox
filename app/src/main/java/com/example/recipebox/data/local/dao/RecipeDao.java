package com.example.recipebox.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.example.recipebox.data.local.entity.RecipeEntity;
import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE isLocal = 1 ORDER BY name ASC")
    List<RecipeEntity> getAllLocalRecipes();

    @Query("SELECT * FROM recipes WHERE id = :id")
    RecipeEntity getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(RecipeEntity recipe);

    @Update
    void update(RecipeEntity recipe);

    @Query("DELETE FROM recipes WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' AND isLocal = 1")
    List<RecipeEntity> searchLocal(String query);
}
