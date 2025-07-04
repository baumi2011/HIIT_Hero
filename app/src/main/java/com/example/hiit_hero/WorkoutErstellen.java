package com.example.hiit_hero;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.EditText;
import android.app.AlertDialog;
import java.util.ArrayList;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Activity zum Erstellen benutzerdefinierter Workouts.
 * Diese Activity ermöglicht es dem Benutzer, eigene Workouts zu erstellen
 * und zu speichern. Sie bietet Eingabefelder für den Workout-Namen, die
 * Dauer, den Kalorienverbrauch und eine Auswahl von Übungen.
 * Die Activity verwendet einen Multi-Choice-Dialog für die Übungsauswahl
 * und speichert das erstellte Workout in der lokalen Datenbank. Sie
 * überprüft auch die Datenspeicherungseinstellungen des Benutzers.
 */

public class WorkoutErstellen extends AppCompatActivity {
    /** Eingabefeld für die ausgewählten Übungen */
    private EditText exercisesInput;
    /** Eingabefeld für den Workout-Namen */
    private EditText workoutNameInput;
    /** Eingabefeld für die Workout-Dauer */
    private EditText timeInput;
    /** Eingabefeld für den Kalorienverbrauch */
    private EditText caloriesInput;
    /** Button zum Speichern und Starten des Workouts */
    private Button startWorkoutButton;
    /** Array mit allen verfügbaren Übungen */
    private final String[] allExercises = new String[]{
            "Liegestütze", "Plank", "Jumping Jacks", "Burpees", "Kniebeugen", "Mountain Climbers"
    };
    /** Array für den Zustand der Übungsauswahl im Dialog */
    private boolean[] checkedExercises;
    /** Liste der ausgewählten Übungsindizes */
    private ArrayList<Integer> selectedExercises;

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, setzt Click-Listener für die
     * Übungsauswahl und den Start-Button. Konfiguriert einen Multi-Choice-Dialog
     * für die Übungsauswahl und implementiert die Validierung und Speicherung
     * der Workout-Daten.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_erstellen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.create_workout_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        exercisesInput = findViewById(R.id.exercisesInput);
        workoutNameInput = findViewById(R.id.workoutNameInput);
        timeInput = findViewById(R.id.timeInput);
        caloriesInput = findViewById(R.id.caloriesInput);
        startWorkoutButton = findViewById(R.id.startWorkoutButton);
        checkedExercises = new boolean[allExercises.length];
        selectedExercises = new ArrayList<>();

        exercisesInput.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(WorkoutErstellen.this);
            builder.setTitle("Übungen auswählen");
            builder.setMultiChoiceItems(allExercises, checkedExercises, (dialog, which, isChecked) -> {
                if (isChecked) {
                    if (!selectedExercises.contains(which)) {
                        selectedExercises.add(which);
                    }
                } else if (selectedExercises.contains(which)) {
                    selectedExercises.remove(Integer.valueOf(which));
                }
            });
            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder selected = new StringBuilder();
                for (int i = 0; i < selectedExercises.size(); i++) {
                    selected.append(allExercises[selectedExercises.get(i)]);
                    if (i != selectedExercises.size() - 1) {
                        selected.append(", ");
                    }
                }
                exercisesInput.setText(selected.toString());
            });
            builder.setNegativeButton("Abbrechen", (dialog, which) -> dialog.dismiss());
            builder.setNeutralButton("Zurücksetzen", (dialog, which) -> {
                for (int i = 0; i < checkedExercises.length; i++) {
                    checkedExercises[i] = false;
                }
                selectedExercises.clear();
                exercisesInput.setText("");
            });
            builder.show();
        });

        startWorkoutButton.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
            if (!prefs.getBoolean("data_collection_enabled", false)) {
                Toast.makeText(this, "Datenspeicherung deaktiviert! Speichern nicht möglich", Toast.LENGTH_SHORT).show();
                return;
            }
            String name = workoutNameInput.getText().toString().trim();
            String duration = timeInput.getText().toString().trim();
            String caloriesStr = caloriesInput.getText().toString().trim();
            String exercisesStr = exercisesInput.getText().toString().trim();

            if (name.isEmpty() || duration.isEmpty() || caloriesStr.isEmpty() || exercisesStr.isEmpty()) {
                Toast.makeText(this, "Bitte alle Felder ausfüllen!", Toast.LENGTH_SHORT).show();
                return;
            }

            int calories;
            try {
                calories = Integer.parseInt(caloriesStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Kalorien muss eine Zahl sein!", Toast.LENGTH_SHORT).show();
                return;
            }

            WorkoutSession workout = new WorkoutSession(name, duration + " Minuten", new java.util.Date().getTime(), calories, exercisesStr);
            new Thread(() -> {
                Datenbank.DatenbaseApp db = Datenbank.DatenbaseApp.getDatabase(getApplicationContext());
                db.userDao().insertWorkout(workout);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Workout gespeichert!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, DeineWorkouts.class);
                    startActivity(intent);
                    finish();
                });
            }).start();
        });
    }
}