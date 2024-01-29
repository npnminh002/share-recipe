package doan.npnm.sharerecipe.database.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import doan.npnm.sharerecipe.model.recipe.Recipe;

@Entity(tableName = "RecipeRoom")
public class RecipeRoom {
    @PrimaryKey(autoGenerate = true)
    public int Id;

    @ColumnInfo(name = "Recipe")
    public String Recipe= new Recipe().toJson();
}
