package com.example.hiit_hero;

import java.util.Date;

public class WorkoutSession {
    private String name;
    private String duration;
    private Date timestamp;
    private int caloriesBurned;

    public WorkoutSession(String name, String duration, Date timestamp, int caloriesBurned) {
        this.name = name;
        this.duration = duration;
        this.timestamp = timestamp;
        this.caloriesBurned = caloriesBurned;
    }

    public String getName() {
        return name;
    }

    public String getDuration() {
        return duration;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }
}
