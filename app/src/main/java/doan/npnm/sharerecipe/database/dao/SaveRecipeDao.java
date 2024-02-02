package doan.npnm.sharerecipe.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.SaveRecipe;

@Dao
public interface SaveRecipeDao {

    @Query("SELECT * FROM SaveRecipe ORDER BY Id DESC LIMIT 10")
    List<SaveRecipe> getListRecentView();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addRecentView(SaveRecipe recipe);

    @Query("DELETE FROM SaveRecipe WHERE RecipeID = :currentID")
    void removeRecent(String currentID);

    @Query("SELECT EXISTS(SELECT 1 FROM SaveRecipe WHERE RecipeID = :currentID)")
    boolean checkExistence(String currentID);
}

