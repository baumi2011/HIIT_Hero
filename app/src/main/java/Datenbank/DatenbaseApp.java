package Datenbank;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class}, version = 1)
public abstract class DatenbaseApp extends RoomDatabase {

    private static DatenbaseApp praxisDbInstance;
    public static DatenbaseApp getInstance(Context context){
        if(DatenbaseApp.praxisDbInstance == null){
            synchronized (DatenbaseApp.class){
                if(DatenbaseApp.praxisDbInstance == null){
                    DatenbaseApp.praxisDbInstance = Room.databaseBuilder(context, DatenbaseApp.class, "HIIT_db").build();
                }
            }
        }
        return(DatenbaseApp.praxisDbInstance);
    }


public abstract DAO userDao();

}
