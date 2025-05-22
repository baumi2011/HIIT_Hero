package com.example.hiit_hero;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        // Set layout managers
        weeklyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        monthlyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up chart button
        Button viewChartButton = findViewById(R.id.viewChartButton);
        viewChartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FortschrittDiagrammActivity.class);
            startActivity(intent);
        });

        // Load workout data
        List<WorkoutSession> weeklyWorkouts = getDummyWeeklyWorkouts();
        List<WorkoutSession> monthlyWorkouts = getDummyMonthlyWorkouts();

        // Set adapters
        WorkoutAdapter weeklyAdapter = new WorkoutAdapter(weeklyWorkouts, dateFormat);
        WorkoutAdapter monthlyAdapter = new WorkoutAdapter(monthlyWorkouts, dateFormat);

        weeklyRecyclerView.setAdapter(weeklyAdapter);
        monthlyRecyclerView.setAdapter(monthlyAdapter);
    }

    private List<WorkoutSession> getDummyWeeklyWorkouts() {
        List<WorkoutSession> workouts = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Add today's workout
        workouts.add(new WorkoutSession("HIIT Cardio", "30 Minuten", calendar.getTime(), 300));

        // Add yesterday's workout
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        workouts.add(new WorkoutSession("HIIT Strength", "45 Minuten", calendar.getTime(), 400));

        return workouts;
    }

    private List<WorkoutSession> getDummyMonthlyWorkouts() {
        List<WorkoutSession> workouts = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        // Add workouts from the last month
        workouts.add(new WorkoutSession("HIIT Cardio", "30 Minuten", calendar.getTime(), 300));

        calendar.add(Calendar.DAY_OF_YEAR, -2);
        workouts.add(new WorkoutSession("HIIT Strength", "45 Minuten", calendar.getTime(), 400));

        calendar.add(Calendar.DAY_OF_YEAR, -5);
        workouts.add(new WorkoutSession("HIIT Cardio", "30 Minuten", calendar.getTime(), 350));

        return workouts;
    }
}