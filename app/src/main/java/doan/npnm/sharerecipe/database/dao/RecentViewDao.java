package doan.npnm.sharerecipe.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import doan.npnm.sharerecipe.database.models.RecentView;

@Dao
public interface RecentViewDao {

    @Query("SELECT * FROM RecentView ORDER BY Id DESC LIMIT 10")
    List<RecentView> getListRecentView();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addRecentView(RecentView recipe);

    @Query("DELETE FROM RecentView WHERE RecipeID = :currentID")
    void removeRecent(String currentID);

    @Query("SELECT EXISTS(SELECT 1 FROM RecentView WHERE RecipeID = :currentID)")
    boolean checkExistence(String currentID);
}

