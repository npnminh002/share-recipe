package doan.npnm.sharerecipe.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Search")
public class Search {
    @PrimaryKey(autoGenerate = true)
    public int Id;
    @ColumnInfo(name = "CurrentKey")
    public String CurrentKey;
}
