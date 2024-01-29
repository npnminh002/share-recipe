package doan.npnm.sharerecipe.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import doan.npnm.sharerecipe.database.models.RecipeRoom;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM RecipeRoom")
    List<RecipeRoom> getAllRecipes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRecipe(RecipeRoom recipe);

}


