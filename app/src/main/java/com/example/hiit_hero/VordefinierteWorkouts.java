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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VordefinierteWorkouts extends AppCompatActivity {
    private TextInputEditText workoutNameInput;
    private TextInputEditText exercisesInput;
    private TextInputEditText timeInput;
    private TextInputEditText caloriesInput;
    private RecyclerView workoutsRecyclerView;
    private WorkoutAdapter adapter;
    private Button startWorkoutButton;

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
            "30 Minuten",
            calendar.getTime(),
            300
        );
        cardioWorkout.addExercise("Burpees");
        cardioWorkout.addExercise("Mountain Climbers");
        cardioWorkout.addExercise("Jumping Jacks");
        workouts.add(cardioWorkout);

        // Beispiel 2: HIIT Strength
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        WorkoutSession strengthWorkout = new WorkoutSession(
            "HIIT Strength",
            "45 Minuten",
            calendar.getTime(),
            400
        );
        strengthWorkout.addExercise("Liegestütze");
        strengthWorkout.addExercise("Kniebeugen");
        strengthWorkout.addExercise("Plank");
        workouts.add(strengthWorkout);

        // Beispiel 3: HIIT Full Body
        calendar.add(Calendar.DAY_OF_YEAR, -2);
        WorkoutSession fullBodyWorkout = new WorkoutSession(
            "HIIT Full Body",
            "40 Minuten",
            calendar.getTime(),
            350
        );
        fullBodyWorkout.addExercise("Burpees");
        fullBodyWorkout.addExercise("Kniebeugen");
        fullBodyWorkout.addExercise("Mountain Climbers");
        fullBodyWorkout.addExercise("Plank");
        workouts.add(fullBodyWorkout);

        adapter = new WorkoutAdapter(workouts, new java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.GERMAN));
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
            startActivity(intent);
        });
    }
}