package doan.npnm.sharerecipe.database;

import androidx.room.Room;

import doan.npnm.sharerecipe.app.context.AppContext;

public class AppDatabaseProvider {
    private static volatile AppDatabase INSTANCE;
    public static synchronized AppDatabase getDatabase() {
        if (INSTANCE == null) {
            synchronized (AppDatabaseProvider.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(AppContext.getContext().getApplicationContext(),
                                    AppDatabase.class, "ShareRecipeDb").allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
