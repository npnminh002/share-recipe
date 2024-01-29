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
    @ColumnInfo(name = "RecipeID")
    public String RecipeID;
    @ColumnInfo(name = "Recipe")
    public String Recipe;

//    public RecentView(int id, String authID, String recipeID, String recipe) {
//        Id = id;
//        AuthID = authID;
//        RecipeID = recipeID;
//        Recipe = recipe;
//    }
//
//    public RecentView() {
//    }
}
