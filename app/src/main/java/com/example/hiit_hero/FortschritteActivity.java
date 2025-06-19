package com.example.hiit_hero;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FortschritteActivity extends AppCompatActivity {
    private RecyclerView weeklyRecyclerView;
    private RecyclerView monthlyRecyclerView;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortschritte);

        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);

        // Initialize RecyclerViews
        weeklyRecyclerView = findViewById(R.id.weeklyRecyclerView);
        monthlyRecyclerView = findViewById(R.id.monthlyRecyclerView);
        TextView weeklyEmptyText = findViewById(R.id.weeklyEmptyText);
        TextView monthlyEmptyText = findViewById(R.id.monthlyEmptyText);

        // Set layout managers
        weeklyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        monthlyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Workouts aus DeineWorkouts holen
        List<WorkoutSession> allWorkouts = DeineWorkouts.eigeneWorkouts;
        List<WorkoutSession> weeklyWorkouts = new ArrayList<>();
        List<WorkoutSession> monthlyWorkouts = new ArrayList<>();
        Date now = new Date();
        long millisInDay = 24 * 60 * 60 * 1000L;

        for (WorkoutSession ws : allWorkouts) {
            long diff = now.getTime() - ws.getDate().getTime();
            if (diff <= 7 * millisInDay) {
                weeklyWorkouts.add(ws);
            } else if (diff <= 31 * millisInDay) {
                monthlyWorkouts.add(ws);
            }
        }

        // Anzeige-Logik
        if (!weeklyWorkouts.isEmpty()) {
            weeklyRecyclerView.setVisibility(View.VISIBLE);
            weeklyEmptyText.setVisibility(View.GONE);
            WorkoutAdapter weeklyAdapter = new WorkoutAdapter(weeklyWorkouts, dateFormat);
            weeklyRecyclerView.setAdapter(weeklyAdapter);
        } else {
            weeklyRecyclerView.setVisibility(View.GONE);
            weeklyEmptyText.setVisibility(View.VISIBLE);
        }

        if (weeklyWorkouts.isEmpty() && !monthlyWorkouts.isEmpty()) {
            monthlyRecyclerView.setVisibility(View.VISIBLE);
            monthlyEmptyText.setVisibility(View.GONE);
            WorkoutAdapter monthlyAdapter = new WorkoutAdapter(monthlyWorkouts, dateFormat);
            monthlyRecyclerView.setAdapter(monthlyAdapter);
        } else {
            monthlyRecyclerView.setVisibility(View.GONE);
            monthlyEmptyText.setVisibility(View.VISIBLE);
        }
    }
}