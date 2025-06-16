package com.example.hiit_hero;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WorkoutSession {
    private String name;
    private String duration;
    private Date date;
    private int caloriesBurned;
    private List<String> exercises;

    public WorkoutSession(String name, String duration, Date date, int caloriesBurned) {
        this.name = name;
        this.duration = duration;
        this.date = date;
        this.caloriesBurned = caloriesBurned;
        this.exercises = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public Date getDate() {
        return date;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public List<String> getExercises() {
        return exercises;
    }

    public void addExercise(String exercise) {
        exercises.add(exercise);
    }

    public String getExercisesAsString() {
        return String.join(", ", exercises);
    }
}