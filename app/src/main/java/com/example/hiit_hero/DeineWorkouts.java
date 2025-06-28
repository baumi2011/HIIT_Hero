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

/**
 * Activity für die Anzeige der benutzerdefinierten Workouts.
 * Diese Activity zeigt alle vom Benutzer erstellten und gespeicherten
 * Workouts in einer RecyclerView an. Sie ermöglicht es dem Benutzer,
 * Workouts zu löschen und zu den Detailansichten zu navigieren.
 * Die Activity lädt Workouts aus der lokalen Datenbank und verwendet
 * einen WorkoutAdapter für die Anzeige. Sie zeigt auch eine Nachricht
 * an, wenn keine Workouts vorhanden sind.
 */

public class DeineWorkouts extends AppCompatActivity {
    /** Adapter für die Workout-Liste */
    private WorkoutAdapter adapter;
    /** Datenbankinstanz */
    private Datenbank.DatenbaseApp db;
    /** Liste der Workouts aus der Datenbank */
    private List<WorkoutSession> workoutsFromDb = new ArrayList<>();

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, konfiguriert die RecyclerView mit
     * dem WorkoutAdapter und setzt Click-Listener für Workout-Aktionen.
     * Lädt auch die gespeicherten Workouts aus der Datenbank.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */
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

    /**
     * Aktualisiert die Anzeige basierend auf dem Vorhandensein von Workouts.
     * Zeigt entweder die RecyclerView mit den Workouts oder eine
     * Nachricht an, wenn keine Workouts vorhanden sind.
     */
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

    /**
     * Lädt alle Workouts aus der Datenbank.
     * Führt die Datenbankabfrage in einem separaten Thread aus und
     * aktualisiert die UI im Hauptthread. Aktualisiert auch den
     * Empty-State nach dem Laden.
     */
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

    /**
     * Wird aufgerufen, wenn die Activity wieder sichtbar wird.
     * Lädt die Workouts neu aus der Datenbank, um sicherzustellen,
     * dass die Anzeige aktuell ist.
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadWorkoutsFromDb();
    }
}