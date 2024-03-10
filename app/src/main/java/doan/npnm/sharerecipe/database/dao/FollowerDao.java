package doan.npnm.sharerecipe.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.database.models.Follower;

@Dao
public interface FollowerDao {

    @Query("SELECT * FROM Follower")
    List<SaveRecipe> getListRecentView();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addRecentView(Follower recipe);

    @Query("DELETE FROM Follower WHERE AuthID  = :currentID")
    void removeRecent(String currentID);

    @Query("SELECT EXISTS(SELECT 1 FROM Follower WHERE AuthID = :currentID)")
    boolean checkExisId(String currentID);

    @Query("DELETE  From  Follower")
    void SignOutApp();
}

