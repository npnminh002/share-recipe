package doan.npnm.sharerecipe.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import doan.npnm.sharerecipe.database.models.LoveRecipe;
import doan.npnm.sharerecipe.database.models.SaveRecipe;

@Dao
public interface LoveRecipeDao {

    @Query("SELECT * FROM LoveRecipe")
    List<LoveRecipe> getLoveArr();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveNewLove(LoveRecipe recipe);

    @Query("DELETE FROM LoveRecipe WHERE RecipeID = :currentID")
    void unLove(String currentID);

    @Query("SELECT EXISTS(SELECT 1 FROM LoveRecipe WHERE RecipeID = :currentID)")
    boolean checkExist(String currentID);

    @Query("DELETE  From  LoveRecipe")
    void SignOutApp();
}

