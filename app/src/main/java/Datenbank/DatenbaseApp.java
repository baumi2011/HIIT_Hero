package Datenbank;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Hauptdatenbankklasse für die HIIT Hero App.
 * Diese Klasse erweitert RoomDatabase und definiert die lokale SQLite-Datenbank
 * der App. Sie implementiert das Singleton-Pattern, um sicherzustellen, dass
 * nur eine Datenbankinstanz existiert.
 * Die Datenbank enthält zwei Entitäten: User und WorkoutSession. Sie verwendet
 * Room für die automatische Generierung von SQL-Code und Datenbankoperationen.
 */
@Database(entities = {User.class, com.example.hiit_hero.WorkoutSession.class}, version = 3, exportSchema = false)
public abstract class DatenbaseApp extends RoomDatabase {
    /** Singleton-Instanz der Datenbank */
    private static volatile DatenbaseApp INSTANCE;
    
    /**
     * Gibt das DAO-Interface für Datenbankoperationen zurück.
     * @return DAO-Interface für Benutzer- und Workout-Operationen
     */
    public abstract DAO userDao();

    /**
     * Gibt die Singleton-Instanz der Datenbank zurück.
     * Erstellt eine neue Datenbankinstanz, falls noch keine existiert.
     * Verwendet doppelte Prüfung für Thread-Sicherheit.
     * @param context Der Application-Context
     * @return Die Datenbankinstanz
     */
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
