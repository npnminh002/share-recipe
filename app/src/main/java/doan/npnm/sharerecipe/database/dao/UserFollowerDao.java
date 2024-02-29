package doan.npnm.sharerecipe.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import doan.npnm.sharerecipe.database.models.Follower;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.database.models.UserFollower;

@Dao
public interface UserFollowerDao {

    @Query("SELECT * FROM UserFollower")
    List<UserFollower> getDataList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addUserFollower(UserFollower data);

    @Query("DELETE FROM UserFollower WHERE AuthID  = :currentID")
    void removeRecent(String currentID);

    @Query("SELECT EXISTS(SELECT 1 FROM UserFollower WHERE AuthID = :currentID)")
    boolean checkExisId(String currentID);
    @Query("DELETE  From  UserFollower")
    void SignOutApp();
}

