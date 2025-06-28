package Datenbank;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity-Klasse für Benutzer in der Datenbank.
 * Diese Klasse repräsentiert einen Benutzer der App und wird von Room
 * verwendet, um Benutzerdaten in der lokalen Datenbank zu speichern und abzurufen.
 * Sie enthält grundlegende Informationen über den Benutzer wie Name, Alter,
 * Gewicht und Größe, die für die Berechnung von Kalorien und anderen
 * Fitness-Metriken verwendet werden.
 */
@Entity(tableName = "users")
public class User {

    /** Primärschlüssel für die Datenbank, wird automatisch generiert */
    @PrimaryKey (autoGenerate = true)
    public int id;

    /** Name des Benutzers */
    @ColumnInfo(name = "name")
    public String name;

    /** Alter des Benutzers in Jahren */
    @ColumnInfo(name = "age")
    public int age;

    /** Gewicht des Benutzers in Kilogramm */
    @ColumnInfo(name = "weight")
    public float weight;

    /** Größe des Benutzers in Zentimetern */
    @ColumnInfo(name = "height")
    public float height;

}
