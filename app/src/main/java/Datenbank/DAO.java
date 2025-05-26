package Datenbank;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAO {

    @Query("SELECT * FROM user")
    List<User> getAll();



}
