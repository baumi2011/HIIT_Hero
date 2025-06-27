package com.example.hiit_hero;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

public class DeineWorkouts extends AppCompatActivity {
    private WorkoutAdapter adapter;
    private Datenbank.DatenbaseApp db;
    private List<WorkoutSession> workoutsFromDb = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deine_workouts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.your_workouts_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = Datenbank.DatenbaseApp.getDatabase(getApplicationContext());
        RecyclerView recyclerView = findViewById(R.id.workoutsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.GERMAN);
        adapter = new WorkoutAdapter(workoutsFromDb, dateFormat, true);
        recyclerView.setAdapter(adapter);

        // Workouts aus Room laden
        loadWorkoutsFromDb();

        // Detailansicht beim Klick
        adapter.setOnItemClickListener(workout -> {
            Intent intent = new Intent(this, WorkoutAnsicht.class);
            intent.putExtra("workoutName", workout.getName());
            intent.putExtra("workoutDuration", workout.getDuration());
            intent.putExtra("workoutDate", dateFormat.format(new java.util.Date(workout.getDate())));
            intent.putExtra("workoutCalories", workout.getCaloriesBurned());
            intent.putExtra("workoutExercises", workout.getExercisesAsString());
            startActivity(intent);
        });

        // Löschen beim Klick auf den Lösch-Button
        adapter.setOnDeleteClickListener(workout -> {
            new AlertDialog.Builder(this)
                    .setTitle("Workout löschen")
                    .setMessage("Möchtest du dieses Workout wirklich löschen?")
                    .setPositiveButton("Ja", (dialog, which) -> {
                        new Thread(() -> {
                            db.userDao().deleteWorkout(workout);
                            runOnUiThread(() -> {
                                adapter.removeWorkout(workout);
                                updateEmptyState();
                            });
                        }).start();
                    })
                    .setNegativeButton("Nein", null)
                    .show();
        });

        TextView emptyWorkoutsText = findViewById(R.id.emptyWorkoutsText);
        updateEmptyState();
    }

    private void updateEmptyState() {
        TextView emptyWorkoutsText = findViewById(R.id.emptyWorkoutsText);
        RecyclerView recyclerView = findViewById(R.id.workoutsRecyclerView);
        if (workoutsFromDb.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyWorkoutsText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyWorkoutsText.setVisibility(View.GONE);
        }
    }

    private void loadWorkoutsFromDb() {
        new Thread(() -> {
            List<WorkoutSession> loaded = db.userDao().getAllWorkouts();
            runOnUiThread(() -> {
                workoutsFromDb.clear();
                workoutsFromDb.addAll(loaded);
                adapter.notifyDataSetChanged();
                updateEmptyState();
            });
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkoutsFromDb();
    }
}