package Datenbank;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DAO {

    @Query("SELECT * FROM users")
    List<User> getAll();

    @Query("SELECT * FROM users LIMIT 1")
    User getFirstUser();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Update
    void update(User user);

}
