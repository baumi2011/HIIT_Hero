package com.example.hiit_hero;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
    private Button backToMainButton;
    private boolean workoutSaved = false;

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
        backToMainButton = findViewById(R.id.backToMainButton);

        // Hole die übergebenen Workout-Werte
        String workoutName = getIntent().getStringExtra("workoutName");
        String workoutDuration = getIntent().getStringExtra("workoutDuration");
        int workoutCalories = getIntent().getIntExtra("workoutCalories", 0);
        String workoutExercises = getIntent().getStringExtra("workoutExercises");
        boolean workoutAborted = getIntent().getBooleanExtra("workoutAborted", false);

        // Aktualisiere die Texte mit den Werten
        if (workoutAborted) {
            congratsText.setText("Workout abgebrochen");
            congratsText.setTextColor(Color.RED);
            durationText.setText("Gesamtzeit: 0");
            caloriesText.setText("Verbrannte Kalorien: 0 kcal");
            saveWorkoutButton.setEnabled(false); // Deaktiviere den Save-Button bei abgebrochenem Workout
        } else {
            congratsText.setText("Geschafft!");
            congratsText.setTextColor(Color.GREEN);
            if (workoutDuration != null) {
                durationText.setText("Gesamtzeit: " + workoutDuration);
            }
            caloriesText.setText("Verbrannte Kalorien: " + workoutCalories + " kcal");
        }

        // Setze den Click-Listener für den Save-Button
        saveWorkoutButton.setOnClickListener(v -> {
            if (!workoutAborted && !workoutSaved) {
                saveWorkout(workoutName, workoutDuration, workoutCalories, workoutExercises);
            }
        });

        // Setze den Click-Listener für den Zurück-Button
        backToMainButton.setOnClickListener(v -> {
            navigateToMainActivity();
        });
    }

    private void saveWorkout(String workoutName, String workoutDuration, int workoutCalories, String workoutExercises) {
        WorkoutSession session = new WorkoutSession(
                workoutName,
                workoutDuration,
                System.currentTimeMillis(),
                workoutCalories,
                workoutExercises
        );
        new Thread(() -> {
            Datenbank.DatenbaseApp.getDatabase(getApplicationContext()).userDao().insertWorkout(session);
            runOnUiThread(() -> {
                Toast.makeText(this, "Workout gespeichert!", Toast.LENGTH_SHORT).show();
                workoutSaved = true;
                saveWorkoutButton.setEnabled(false);
            });
        }).start();
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutFertigActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Wenn der Zurück-Button des Geräts gedrückt wird, zum Hauptmenü navigieren
        navigateToMainActivity();
    }
}