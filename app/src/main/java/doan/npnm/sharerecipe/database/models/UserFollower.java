package doan.npnm.sharerecipe.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "UserFollower")
public class UserFollower {
    @PrimaryKey(autoGenerate = true)
    public int Id;
    @ColumnInfo(name = "AuthID")
    public String AuthID;
}
