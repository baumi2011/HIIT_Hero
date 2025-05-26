package Datenbank;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class DatenbaseApp extends RoomDatabase {
    public abstract DAO userDao();



}
