package com.example.hiit_hero;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Activity für die Workout-Navigation.
 * Diese Activity dient als zentraler Einstiegspunkt für alle Workout-bezogenen
 * Funktionen der App. Sie bietet Buttons zur Navigation zu vordefinierten
 * Workouts, zum Erstellen eigener Workouts, zur Anzeige der eigenen Workouts
 * und zur Übungsübersicht.
 * Die Activity fungiert als Hub für die verschiedenen Workout-Bereiche
 * und ermöglicht eine intuitive Navigation zwischen den verschiedenen
 * Workout-Funktionen.
 */

public class Workouts extends AppCompatActivity {

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente und setzt Click-Listener für alle
     * Navigationsbuttons. Jeder Button führt zu einer anderen Workout-
     * Funktionalität der App.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workouts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.workouts_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up click listeners for all buttons
        Button predefinedWorkoutsButton = findViewById(R.id.predefinedWorkoutsButton);
        Button createWorkoutButton = findViewById(R.id.createWorkoutButton);
        Button yourWorkoutsButton = findViewById(R.id.yourWorkoutsButton);
        Button uebungenButton = findViewById(R.id.uebungenButton);

        predefinedWorkoutsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, VordefinierteWorkouts.class);
            startActivity(intent);
        });

        createWorkoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutErstellen.class);
            startActivity(intent);
        });

        yourWorkoutsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeineWorkouts.class);
            startActivity(intent);
        });

        uebungenButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Uebungen.class);
            startActivity(intent);
        });
    }
}