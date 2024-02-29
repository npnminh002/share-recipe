package doan.npnm.sharerecipe.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Follower")
public class Follower {
    @PrimaryKey(autoGenerate = true)
    public int Id;
    @ColumnInfo(name = "AuthID")
    public String AuthID;
}
