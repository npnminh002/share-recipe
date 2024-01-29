package doan.npnm.sharerecipe.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import doan.npnm.sharerecipe.database.dao.RecentViewDao;
import doan.npnm.sharerecipe.database.dao.RecipeDao;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.RecipeRoom;

@Database(entities = {RecentView.class,RecipeRoom.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
    public abstract RecentViewDao recentViewDao();
}


