package doan.npnm.sharerecipe.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LoveRecipe")
public class LoveRecipe {
    @PrimaryKey(autoGenerate = true)
    public int Id;
    @ColumnInfo(name = "RecipeID")
    public String RecipeID;
}
