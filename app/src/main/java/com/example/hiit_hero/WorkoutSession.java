package com.example.hiit_hero;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

/**
 * Entity-Klasse für Workout-Sessions in der Datenbank.
 * Diese Klasse repräsentiert eine einzelne Workout-Session und wird von Room
 * verwendet, um Workout-Daten in der lokalen Datenbank zu speichern und abzurufen.
 * Sie enthält alle relevanten Informationen über ein durchgeführtes Workout,
 * einschließlich Name, Dauer, Datum, verbrannte Kalorien und durchgeführte Übungen.
 * Die Klasse unterstützt sowohl normale Workouts mit verschiedenen Übungen als auch
 * Lauf-Workouts mit Distanz- und Geschwindigkeitsdaten.
 */

@Entity(tableName = "workouts")
public class WorkoutSession {
    /** Primärschlüssel für die Datenbank, wird automatisch generiert */
    @PrimaryKey(autoGenerate = true)
    public int id;

    /** Name des Workouts */
    @ColumnInfo(name = "name")
    public String name;

    /** Dauer des Workouts als String */
    @ColumnInfo(name = "duration")
    public String duration;

    /** Zeitstempel des Workouts in Millisekunden */
    @ColumnInfo(name = "date")
    public long date; // Timestamp

    /** Anzahl der verbrannten Kalorien */
    @ColumnInfo(name = "caloriesBurned")
    public int caloriesBurned;

    /** Liste der durchgeführten Übungen als komma-separierter String */
    @ColumnInfo(name = "exercises")
    public String exercises; // Komma-separiert

    /** Zurückgelegte Distanz in Kilometern (für Lauf-Workouts) */
    @ColumnInfo(name = "distance_km")
    public float distanceKm;

    /** Durchschnittsgeschwindigkeit in km/h (für Lauf-Workouts) */
    @ColumnInfo(name = "avg_speed_kmh")
    public float avgSpeedKmh;

    /**
     * Konstruktor für normale Workouts mit Übungen.
     * Erstellt eine neue WorkoutSession für ein Workout mit verschiedenen
     * Übungen. Die Übungen werden als komma-separierter String gespeichert.
     * @param name Name des Workouts
     * @param duration Dauer des Workouts als String
     * @param date Zeitstempel des Workouts
     * @param caloriesBurned Anzahl der verbrannten Kalorien
     * @param exercises Liste der Übungen als komma-separierter String
     */

    @Ignore
    public WorkoutSession(String name, String duration, long date, int caloriesBurned, String exercises) {
        this.name = name;
        this.duration = duration;
        this.date = date;
        this.caloriesBurned = caloriesBurned;
        this.exercises = exercises;
    }

    /**
     * Konstruktor für Lauf-Workouts.
     * Erstellt eine neue WorkoutSession für ein Lauf-Workout mit
     * Distanz- und Geschwindigkeitsdaten. Die Übung wird automatisch
     * auf "Laufen" gesetzt.
     * @param name Name des Workouts
     * @param duration Dauer des Workouts als String
     * @param date Zeitstempel des Workouts
     * @param caloriesBurned Anzahl der verbrannten Kalorien
     * @param distanceKm Zurückgelegte Distanz in Kilometern
     * @param avgSpeedKmh Durchschnittsgeschwindigkeit in km/h
     */

    @Ignore
    public WorkoutSession(String name, String duration, long date, int caloriesBurned, float distanceKm, float avgSpeedKmh) {
        this.name = name;
        this.duration = duration;
        this.date = date;
        this.caloriesBurned = caloriesBurned;
        this.distanceKm = distanceKm;
        this.avgSpeedKmh = avgSpeedKmh;
        this.exercises = "Laufen"; // Set exercise type
    }

    /**
     * Leerer Konstruktor für Room.
     * Wird von Room benötigt, um Objekte aus der Datenbank zu erstellen.
     */

    public WorkoutSession() {}

    /**
     * Gibt den Namen des Workouts zurück.
     * @return Name des Workouts
     */

    public String getName() { return name; }

    /**
     * Gibt die Dauer des Workouts zurück.
     * @return Dauer des Workouts als String
     */
    public String getDuration() { return duration; }

    /**
     * Gibt den Zeitstempel des Workouts zurück.
     * @return Zeitstempel in Millisekunden
     */
    public long getDate() { return date; }

    /**
     * Gibt die Anzahl der verbrannten Kalorien zurück.
     * @return Anzahl der verbrannten Kalorien
     */
    public int getCaloriesBurned() { return caloriesBurned; }

    /**
     * Gibt die Übungen als String zurück.
     * @return Übungen als komma-separierter String
     */
    public String getExercisesAsString() { return exercises; }

    /**
     * Gibt die Übungen als Array zurück.
     * Teilt den komma-separierten String in ein Array von Übungsnamen auf.
     * @return Array der Übungsnamen
     */
    public String[] getExercisesList() {
        return exercises != null ? exercises.split(", ") : new String[0];
    }

    /**
     * Gibt die zurückgelegte Distanz in Kilometern zurück.
     * @return Distanz in Kilometern
     */
    public float getDistanceKm() { return distanceKm; }

    /**
     * Gibt die durchschnittliche Geschwindigkeit in km/h zurück.
     * @return Durchschnittsgeschwindigkeit in km/h
     */
    public float getAvgSpeedKmh() { return avgSpeedKmh; }
}