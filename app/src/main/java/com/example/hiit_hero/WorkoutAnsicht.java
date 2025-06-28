package com.example.hiit_hero;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Activity für die detaillierte Ansicht eines Workouts.
 * Diese Activity zeigt detaillierte Informationen über ein ausgewähltes
 * Workout an, einschließlich Name, Dauer, Kalorienverbrauch und Liste
 * der enthaltenen Übungen. Sie ermöglicht es dem Benutzer, das Workout
 * zu starten und zur Exercise-Activity zu navigieren.
 * Die Activity empfängt die Workout-Daten über Intent-Extras und
 * bereitet diese für die Anzeige und den Workout-Start vor.
 */

public class WorkoutAnsicht extends AppCompatActivity {

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, lädt die Workout-Daten aus dem Intent
     * und zeigt diese in der entsprechenden Form an. Setzt auch den
     * Click-Listener für den Start-Button, um das Workout zu beginnen.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_ansicht);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.workout_detail_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Views finden
        TextView title = findViewById(R.id.workoutDetailTitle);
        TextView exercisesList = findViewById(R.id.exercisesList);
        TextView timeValue = findViewById(R.id.timeValue);
        TextView caloriesValue = findViewById(R.id.caloriesValue);
        Button startWorkoutButton = findViewById(R.id.startWorkoutButton);

        // Daten aus Intent holen
        String name = getIntent().getStringExtra("workoutName");
        String duration = getIntent().getStringExtra("workoutDuration");
        String exercises = getIntent().getStringExtra("workoutExercises");
        int calories = getIntent().getIntExtra("workoutCalories", 0);

        // Anzeigen
        title.setText(name != null ? name : "Workout");
        exercisesList.setText(exercises != null ? exercises.replace(", ", "\n") : "");
        timeValue.setText(duration != null ? duration : "");
        caloriesValue.setText(calories + " kcal");

        // Workout starten Button
        startWorkoutButton.setOnClickListener(v -> {
            if (exercises != null && !exercises.isEmpty()) {
                ArrayList<String> exerciseList = new ArrayList<>(Arrays.asList(exercises.split(", ")));
                Intent intent = new Intent(WorkoutAnsicht.this, Exercise.class);
                intent.putExtra("EXERCISE_NAME", exerciseList.get(0));
                intent.putStringArrayListExtra("exercises", exerciseList);
                intent.putExtra("currentExerciseIndex", 0);
                intent.putExtra("workoutDuration", duration);
                intent.putExtra("workoutCalories", calories);
                startActivity(intent);
            }
        });
    }
}