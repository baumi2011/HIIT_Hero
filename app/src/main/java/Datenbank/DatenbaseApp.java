package Datenbank;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1, exportSchema = false)
public abstract class DatenbaseApp extends RoomDatabase {
    private static volatile DatenbaseApp INSTANCE;
    public abstract DAO userDao();

    public static DatenbaseApp getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DatenbaseApp.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            DatenbaseApp.class, "hiit_hero_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
