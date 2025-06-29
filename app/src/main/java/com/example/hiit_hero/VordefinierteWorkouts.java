package com.example.hiit_hero;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Arrays;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Activity für vordefinierte Workouts.
 * Diese Activity zeigt eine Liste von vordefinierten Workouts an und
 * ermöglicht es dem Benutzer, diese auszuwählen und zu starten. Sie
 * bietet auch die Möglichkeit, direkt zu einem Lauf-Workout zu navigieren.
 * Die Activity verwendet eine RecyclerView mit einem WorkoutAdapter,
 * um die vordefinierten Workouts anzuzeigen. Der Benutzer kann ein
 * Workout auswählen, dessen Details werden dann in Eingabefeldern
 * angezeigt und können gestartet werden.
 */

public class VordefinierteWorkouts extends AppCompatActivity {
    /** Eingabefeld für den Workout-Namen */
    private TextInputEditText workoutNameInput;
    /** Eingabefeld für die Übungen */
    private TextInputEditText exercisesInput;
    /** Eingabefeld für die Dauer */
    private TextInputEditText timeInput;
    /** Eingabefeld für die Kalorien */
    private TextInputEditText caloriesInput;
    /** RecyclerView für die Workout-Liste */
    private RecyclerView workoutsRecyclerView;
    /** Adapter für die Workout-Liste */
    private WorkoutAdapter adapter;
    /** Button zum Starten des ausgewählten Workouts */
    private Button startWorkoutButton;
    /** Button zum Starten eines Lauf-Workouts */
    private Button startRunningButton;

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, erstellt eine Liste von vordefinierten
     * Workouts und konfiguriert die RecyclerView mit dem WorkoutAdapter.
     * Setzt Click-Listener für alle interaktiven Elemente und implementiert
     * die Logik für die Workout-Auswahl und das Starten von Workouts.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vordefinierte_workouts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.predefined_workouts_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisiere die Views
        workoutNameInput = findViewById(R.id.workoutNameInput);
        exercisesInput = findViewById(R.id.exercisesInput);
        timeInput = findViewById(R.id.timeInput);
        caloriesInput = findViewById(R.id.caloriesInput);
        workoutsRecyclerView = findViewById(R.id.workoutsRecyclerView);
        startWorkoutButton = findViewById(R.id.startWorkoutButton);
        startRunningButton = findViewById(R.id.startRunningButton);

        // Setze die RecyclerView standardmäßig auf unsichtbar
        workoutsRecyclerView.setVisibility(View.GONE);

        // Setze den LayoutManager für die RecyclerView
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Erstelle eine Liste mit Beispiel-Workouts
        List<WorkoutSession> workouts = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        
        // Beispiel 1: HIIT Cardio
        WorkoutSession cardioWorkout = new WorkoutSession(
            "HIIT Cardio",
            "2 Minuten",
            calendar.getTimeInMillis(),
            16,
            "Burpees, Mountain Climbers, Jumping Jacks"
        );
        workouts.add(cardioWorkout);

        // Beispiel 2: HIIT Strength
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        WorkoutSession strengthWorkout = new WorkoutSession(
            "HIIT Strength",
            "2 Minuten",
            calendar.getTimeInMillis(),
            10,
            "Liegestütze, Kniebeugen, Plank"
        );
        workouts.add(strengthWorkout);

        // Beispiel 3: HIIT Full Body
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        WorkoutSession fullBodyWorkout = new WorkoutSession(
            "HIIT Full Body",
            "2:45 Minuten",
            calendar.getTimeInMillis(),
            18,
            "Burpees, Kniebeugen, Mountain Climbers, Plank"
        );
        workouts.add(fullBodyWorkout);

        adapter = new WorkoutAdapter(workouts, new java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.GERMAN), false);
        workoutsRecyclerView.setAdapter(adapter);

        // Setze den Click-Listener für das Workout-Textfeld
        workoutNameInput.setOnClickListener(v -> {
            // Wechsle die Sichtbarkeit der RecyclerView
            if (workoutsRecyclerView.getVisibility() == View.VISIBLE) {
                workoutsRecyclerView.setVisibility(View.GONE);
            } else {
                workoutsRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Setze den Click-Listener für das Übungen-Textfeld
        exercisesInput.setOnClickListener(v -> {
            // Wenn ein Workout ausgewählt ist, zeige dessen Übungen an
            String selectedWorkout = workoutNameInput.getText().toString();
            if (!selectedWorkout.isEmpty()) {
                for (WorkoutSession workout : workouts) {
                    if (workout.getName().equals(selectedWorkout)) {
                        exercisesInput.setText(workout.getExercisesAsString());
                        break;
                    }
                }
            }
        });

        // Setze den Click-Listener für die Workout-Items
        adapter.setOnItemClickListener(workout -> {
            // Fülle die Textfelder mit den Workout-Daten
            workoutNameInput.setText(workout.getName());
            exercisesInput.setText(workout.getExercisesAsString());
            timeInput.setText(workout.getDuration());
            caloriesInput.setText(String.valueOf(workout.getCaloriesBurned()));
            
            // Verstecke die RecyclerView
            workoutsRecyclerView.setVisibility(View.GONE);
        });

        // Setze den Click-Listener für den "Workout starten" Button
        startWorkoutButton.setOnClickListener(v -> {
            // Navigiere zur Exercise-Activity
            Intent intent = new Intent(VordefinierteWorkouts.this, Exercise.class);
            // Übergebe den Namen der ersten Übung und die komplette Übungsliste
            String selectedWorkout = workoutNameInput.getText().toString();
            if (!selectedWorkout.isEmpty()) {
                for (WorkoutSession workout : workouts) {
                    if (workout.getName().equals(selectedWorkout)) {
                        java.util.List<String> exercises = Arrays.asList(workout.getExercisesList());
                        if (!exercises.isEmpty()) {
                            intent.putExtra("EXERCISE_NAME", exercises.get(0));
                            intent.putStringArrayListExtra("exercises", new ArrayList<>(exercises));
                            intent.putExtra("currentExerciseIndex", 0);
                            
                            // Übergebe die Workout-Werte direkt aus dem WorkoutSession-Objekt
                            String duration = workout.getDuration();
                            int calories = workout.getCaloriesBurned();
                            
                            // Debug-Ausgabe
                            System.out.println("Workout Duration: " + duration);
                            System.out.println("Workout Calories: " + calories);
                            
                            intent.putExtra("workoutDuration", duration);
                            intent.putExtra("workoutCalories", calories);
                            break;
                        }
                    }
                }
            }
            startActivity(intent);
        });

        startRunningButton.setOnClickListener(v -> {
            Intent intent = new Intent(VordefinierteWorkouts.this, RunningActivity.class);
            startActivity(intent);
        });
    }
}