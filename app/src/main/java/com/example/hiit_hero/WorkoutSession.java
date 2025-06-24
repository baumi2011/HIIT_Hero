package com.example.hiit_hero;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
@Entity(tableName = "workouts")

public class WorkoutSession {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "duration")
    public String duration;

    @ColumnInfo(name = "date")
    public long date; // Timestamp

    @ColumnInfo(name = "caloriesBurned")
    public int caloriesBurned;

    @ColumnInfo(name = "exercises")
    public String exercises; // Komma-separiert

    @Ignore
    public WorkoutSession(String name, String duration, long date, int caloriesBurned, String exercises) {
        this.name = name;
        this.duration = duration;
        this.date = date;
        this.caloriesBurned = caloriesBurned;
        this.exercises = exercises;
    }

    // Leerer Konstruktor für Room
    public WorkoutSession() {}

    public String getName() { return name; }
    public String getDuration() { return duration; }
    public long getDate() { return date; }
    public int getCaloriesBurned() { return caloriesBurned; }
    public String getExercisesAsString() { return exercises; }
    public String[] getExercisesList() {
        return exercises != null ? exercises.split(", ") : new String[0];
    }
}