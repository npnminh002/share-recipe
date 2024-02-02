package doan.npnm.sharerecipe.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "RecentView")
public class RecentView {
    @PrimaryKey(autoGenerate = true)
    public int Id;
    @ColumnInfo(name = "AuthID")
    public String AuthID;
    @ColumnInfo(name = "ViewTime")
    public String ViewTime;
    @ColumnInfo(name = "RecipeID")
    public String RecipeID;

    @ColumnInfo(name = "Recipe")
    public String Recipe;
}

