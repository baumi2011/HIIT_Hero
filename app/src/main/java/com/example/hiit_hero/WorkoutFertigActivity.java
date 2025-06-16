package com.example.hiit_hero;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WorkoutFertigActivity extends AppCompatActivity {
    private TextView congratsText;
    private TextView caloriesText;
    private TextView durationText;
    private Button saveWorkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_fertig);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisiere die Views
        congratsText = findViewById(R.id.congratsText);
        caloriesText = findViewById(R.id.caloriesText);
        durationText = findViewById(R.id.durationText);
        saveWorkoutButton = findViewById(R.id.saveWorkoutButton);

        // Setze den festen Text für congratsText
        congratsText.setText("Geschafft!");

        // Hole die übergebenen Workout-Werte
        String workoutDuration = getIntent().getStringExtra("workoutDuration");
        int workoutCalories = getIntent().getIntExtra("workoutCalories", 0);

        // Aktualisiere die Texte mit den Werten
        if (workoutDuration != null) {
            durationText.setText("Gesamtzeit: " + workoutDuration);
        }
        caloriesText.setText("Verbrannte Kalorien: " + workoutCalories + " kcal");

        // Setze den Click-Listener für den Save-Button
        saveWorkoutButton.setOnClickListener(v -> {
            // Navigiere zurück zur MainActivity
            Intent intent = new Intent(WorkoutFertigActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}