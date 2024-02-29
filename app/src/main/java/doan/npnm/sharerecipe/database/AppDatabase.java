package doan.npnm.sharerecipe.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import doan.npnm.sharerecipe.database.dao.FollowerDao;
import doan.npnm.sharerecipe.database.dao.RecentViewDao;
import doan.npnm.sharerecipe.database.dao.RecipeDao;
import doan.npnm.sharerecipe.database.dao.SaveRecipeDao;
import doan.npnm.sharerecipe.database.dao.SearchDao;
import doan.npnm.sharerecipe.database.dao.UserFollowerDao;
import doan.npnm.sharerecipe.database.models.Follower;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.RecipeRoom;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.database.models.Search;
import doan.npnm.sharerecipe.database.models.UserFollower;

@Database(entities = {RecentView.class,RecipeRoom.class, SaveRecipe.class, Follower.class, Search.class, UserFollower.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RecipeDao recipeDao();
    public abstract RecentViewDao recentViewDao();
    public abstract SaveRecipeDao saveRecipeDao();
    public abstract FollowerDao followerDao();
    public abstract SearchDao searchDao();

    public abstract UserFollowerDao userFollowerDao();
}


