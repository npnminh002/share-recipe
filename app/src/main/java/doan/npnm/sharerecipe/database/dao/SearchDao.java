package doan.npnm.sharerecipe.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import doan.npnm.sharerecipe.database.models.Search;

@Dao
public interface SearchDao {
    @Query("SELECT * FROM Search ORDER BY Id DESC")
    List<Search> getListCurrent();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addRecentView(Search recipe);

    @Query("DELETE FROM Search WHERE Id  = :currentID")
    void removeRecent(int currentID);


    @Query("DELETE  From  search")
    void SignOutApp();
}

