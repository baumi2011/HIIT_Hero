package Datenbank;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import java.util.List;

/**
 * Data Access Object (DAO) Interface für Datenbankoperationen.
 * Dieses Interface definiert alle Datenbankoperationen für die HIIT Hero App.
 * Es verwendet Room-Annotationen, um SQL-Abfragen und Datenbankoperationen
 * für Benutzer und Workout-Sessions zu definieren.
 * Das Interface unterstützt CRUD-Operationen (Create, Read, Update, Delete)
 * für beide Entitäten: User und WorkoutSession.
 */
@Dao
public interface DAO {

    /**
     * Ruft alle Benutzer aus der Datenbank ab.
     * @return Liste aller Benutzer in der Datenbank
     */
    @Query("SELECT * FROM users")
    List<User> getAll();

    /**
     * Ruft den ersten Benutzer aus der Datenbank ab.
     * Wird verwendet, um den aktuellen Benutzer zu erhalten,
     * da die App nur einen Benutzer unterstützt.
     * @return Der erste Benutzer in der Datenbank oder null
     */
    @Query("SELECT * FROM users LIMIT 1")
    User getFirstUser();

    /**
     * Fügt einen neuen Benutzer in die Datenbank ein.
     * Bei Konflikten wird der bestehende Eintrag ersetzt.
     * @param user Der Benutzer, der eingefügt werden soll
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    /**
     * Aktualisiert einen bestehenden Benutzer in der Datenbank.
     * @param user Der Benutzer mit den aktualisierten Daten
     */
    @Update
    void update(User user);

    /**
     * Ruft alle Workout-Sessions aus der Datenbank ab.
     * @return Liste aller gespeicherten Workout-Sessions
     */
    @Query("SELECT * FROM workouts")
    List<com.example.hiit_hero.WorkoutSession> getAllWorkouts();

    /**
     * Fügt eine neue Workout-Session in die Datenbank ein.
     * @param workout Die Workout-Session, die eingefügt werden soll
     */
    @Insert
    void insertWorkout(com.example.hiit_hero.WorkoutSession workout);

    /**
     * Löscht eine Workout-Session aus der Datenbank.
     * @param workout Die Workout-Session, die gelöscht werden soll
     */
    @Delete
    void deleteWorkout(com.example.hiit_hero.WorkoutSession workout);

    /**
     * Aktualisiert eine bestehende Workout-Session in der Datenbank.
     * @param workout Die Workout-Session mit den aktualisierten Daten
     */
    @Update
    void updateWorkout(com.example.hiit_hero.WorkoutSession workout);

}
