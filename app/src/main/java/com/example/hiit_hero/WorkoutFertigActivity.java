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

/**
 * Activity, die nach Abschluss oder Abbruch eines Workouts angezeigt wird.
 * Diese Activity zeigt eine Zusammenfassung des durchgeführten Workouts an,
 * einschließlich der verbrachten Zeit, verbrannten Kalorien und der Möglichkeit,
 * das Workout in der Datenbank zu speichern. Sie unterscheidet zwischen
 * erfolgreich abgeschlossenen und abgebrochenen Workouts.
 */

public class WorkoutFertigActivity extends AppCompatActivity {
    /** TextView für die Glückwunsch- oder Abbruch-Nachricht */
    private TextView congratsText;
    /** TextView für die Anzeige der verbrannten Kalorien */
    private TextView caloriesText;
    /** TextView für die Anzeige der Workout-Dauer */
    private TextView durationText;
    /** Button zum Speichern des Workouts in der Datenbank */
    private Button saveWorkoutButton;
    /** Button zur Rückkehr zum Hauptmenü */
    private Button backToMainButton;
    /** Flag, das angibt, ob das Workout bereits gespeichert wurde */
    private boolean workoutSaved = false;

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, empfängt die Workout-Daten über Intent-Extras
     * und konfiguriert die Anzeige basierend darauf, ob das Workout abgeschlossen
     * oder abgebrochen wurde. Setzt auch die Click-Listener für die Buttons.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

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

    /**
     * Speichert das abgeschlossene Workout in der Datenbank.
     * Erstellt ein neues WorkoutSession-Objekt mit den übergebenen Parametern
     * und fügt es asynchron in die Datenbank ein. Zeigt eine Bestätigungsnachricht
     * an und deaktiviert den Save-Button nach erfolgreichem Speichern.
     * @param workoutName Name des Workouts
     * @param workoutDuration Dauer des Workouts als String
     * @param workoutCalories Anzahl der verbrannten Kalorien
     * @param workoutExercises Liste der durchgeführten Übungen als String
     */
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

    /**
     * Navigiert zurück zur MainActivity und löscht den Activity-Stack.
     * Erstellt einen neuen Intent für die MainActivity und setzt Flags,
     * um alle anderen Activities im Stack zu löschen. Dadurch wird
     * sichergestellt, dass der Benutzer zum Hauptmenü zurückkehrt.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(WorkoutFertigActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Überschreibt das Standard-Verhalten des Zurück-Buttons.
     * Wenn der Benutzer den Zurück-Button des Geräts drückt, wird
     * automatisch zur MainActivity navigiert, anstatt zur vorherigen
     * Activity zurückzukehren.
     */

    @Override
    public void onBackPressed() {
        // Wenn der Zurück-Button des Geräts gedrückt wird, zum Hauptmenü navigieren
        navigateToMainActivity();
    }
}