package com.example.hiit_hero;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import android.view.View;
import androidx.core.content.ContextCompat;

/**
 * Activity für die Pause zwischen Übungen während eines Workouts.
 * 
 * Diese Activity zeigt eine Pause zwischen den einzelnen Übungen an und
 * ermöglicht es dem Benutzer, sich zu erholen. Sie zeigt einen Countdown-Timer
 * für die Pausenzeit und eine Vorschau der nächsten Übung mit entsprechendem
 * Bild an.
 * Die Activity startet automatisch einen 5-Sekunden-Timer und navigiert
 * nach Ablauf zur nächsten Übung oder beendet das Workout, wenn es die
 * letzte Übung war. Der Timer kann gestoppt und wieder gestartet werden.
 */

public class PauseActivity extends AppCompatActivity {
    /** TextView für die Anzeige des Pausen-Timers */
    private TextView pauseTimer;
    /** TextView für die Anzeige der nächsten Übung */
    private TextView nextExerciseText;
    /** Button zum Starten/Stoppen des Timers */
    private Button controlButton;
    /** Button zum Abbrechen des gesamten Workouts */
    private Button abortButton;
    /** CountDownTimer für die Pausenzeit */
    private CountDownTimer countDownTimer;
    /** Flag, das angibt, ob der Timer läuft */
    private boolean isTimerRunning = false;
    /** Verbleibende Zeit in Millisekunden (Standard: 5 Sekunden) */
    private long timeLeftInMillis = 5000; // 5 Sekunden
    /** Liste aller Übungen im aktuellen Workout */
    private ArrayList<String> exercises;
    /** Index der aktuellen Übung in der Übungsliste */
    private int currentExerciseIndex;

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, lädt die Übungsliste und den aktuellen
     * Index aus dem Intent, zeigt die nächste Übung an und startet automatisch
     * den Pausen-Timer. Konfiguriert auch die Click-Listener für alle Buttons.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pause);

        View mainView = findViewById(R.id.main);
        mainView.setBackgroundColor(ContextCompat.getColor(this, R.color.pause_background));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hole die Übungsliste und den aktuellen Index aus dem Intent
        exercises = getIntent().getStringArrayListExtra("exercises");
        currentExerciseIndex = getIntent().getIntExtra("currentExerciseIndex", 0);

        // Initialisiere die Views
        pauseTimer = findViewById(R.id.pauseTimer);
        nextExerciseText = findViewById(R.id.nextExerciseText);
        controlButton = findViewById(R.id.continueButton);
        abortButton = findViewById(R.id.abortButton);
        controlButton.setText("Stop");

        // Zeige die nächste Übung an
        updateNextExercise();

        // Starte den Timer automatisch
        startTimer();

        // Setze den Click-Listener für den Control-Button
        controlButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                stopTimer();
            } else {
                startTimer();
            }
        });

        abortButton.setOnClickListener(v -> {
            Intent intent = new Intent(PauseActivity.this, WorkoutFertigActivity.class);
            intent.putExtra("workoutAborted", true);
            // Übergebe die Workout-Werte
            String duration = getIntent().getStringExtra("workoutDuration");
            int calories = getIntent().getIntExtra("workoutCalories", 0);
            intent.putExtra("workoutDuration", duration);
            intent.putExtra("workoutCalories", calories);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Aktualisiert die Anzeige der nächsten Übung.
     * Lädt das entsprechende Bild für die nächste Übung und zeigt
     * den Übungsnamen an. Bei Jumping Jacks wird ein zusätzlicher
     * Hinweis angezeigt. Zeigt "Workout beenden" an, wenn es die
     * letzte Übung war.
     */
    private void updateNextExercise() {
        if (exercises != null && currentExerciseIndex + 1 < exercises.size()) {
            String nextExercise = exercises.get(currentExerciseIndex + 1);
            ImageView nextExerciseImage = findViewById(R.id.nextExerciseImage);
            TextView jumpingJackHint = findViewById(R.id.jumpingJackHint);
            jumpingJackHint.setVisibility(View.GONE);
            int imageRes = R.drawable.ic_launcher_foreground;
            switch (nextExercise) {
                case "Liegestütze":
                    imageRes = R.drawable.pushups;
                    break;
                case "Plank":
                    imageRes = R.drawable.plank;
                    break;
                case "Jumping Jacks":
                    imageRes = R.drawable.jumpingjacks;
                    jumpingJackHint.setVisibility(View.VISIBLE);
                    break;
                case "Burpees":
                    imageRes = R.drawable.burpees;
                    break;
                case "Kniebeugen":
                    imageRes = R.drawable.kniebeugen;
                    break;
                case "Mountain Climbers":
                    imageRes = R.drawable.mountainclimbers;
                    break;
            }
            Glide.with(this).asGif().load(imageRes).into(nextExerciseImage);
            if (currentExerciseIndex + 2 >= exercises.size()) {
                nextExerciseText.setText("Letzte Übung: " + nextExercise);
            } else {
                nextExerciseText.setText("Nächste Übung: " + nextExercise);
            }
        } else {
            nextExerciseText.setText("Workout beenden");
        }
    }

    /**
     * Startet den Countdown-Timer für die Pause.
     * Erstellt einen neuen CountDownTimer, der für 5 Sekunden läuft
     * und die Anzeige jede Sekunde aktualisiert. Nach Ablauf wird
     * automatisch zur nächsten Übung oder zur WorkoutFertigActivity
     * navigiert, je nachdem ob es weitere Übungen gibt.
     */
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                timeLeftInMillis = 5000; // Reset auf 5 Sekunden
                updateCountDownText();
                controlButton.setText("Start");
                // Hole die Workout-Werte aus dem Intent, um sie weiterzugeben
                String workoutName = getIntent().getStringExtra("workoutName");
                String duration = getIntent().getStringExtra("workoutDuration");
                int calories = getIntent().getIntExtra("workoutCalories", 0);
                String exercisesString = String.join(", ", exercises);

                // Wenn es die letzte Übung war, navigiere zur WorkoutFertigActivity
                if (exercises != null && currentExerciseIndex + 1 >= exercises.size()) {
                    Intent intent = new Intent(PauseActivity.this, WorkoutFertigActivity.class);
                    // Übergebe die Workout-Werte
                    intent.putExtra("workoutName", workoutName);
                    intent.putExtra("workoutDuration", duration);
                    intent.putExtra("workoutCalories", calories);
                    intent.putExtra("workoutExercises", exercisesString);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Sonst zur nächsten Übung
                    Intent intent = new Intent(PauseActivity.this, Exercise.class);
                    intent.putExtra("EXERCISE_NAME", exercises.get(currentExerciseIndex + 1));
                    intent.putStringArrayListExtra("exercises", exercises);
                    intent.putExtra("currentExerciseIndex", currentExerciseIndex + 1);
                    // Übergebe die Workout-Werte weiter
                    intent.putExtra("workoutName", workoutName);
                    intent.putExtra("workoutDuration", duration);
                    intent.putExtra("workoutCalories", calories);
                    startActivity(intent);
                    finish();
                }
            }
        }.start();

        isTimerRunning = true;
        controlButton.setText("Stop");
    }

    /**
     * Stoppt den laufenden Timer.
     * Bricht den CountDownTimer ab und setzt die Button-Zustände zurück.
     * Der Timer kann später wieder gestartet werden.
     */
    private void stopTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        controlButton.setText("Start");
    }

    /**
     * Aktualisiert die Timer-Anzeige.
     * Konvertiert die verbleibende Zeit in Sekunden und formatiert
     * sie als zweistellige Zahl für die Anzeige.
     */
    private void updateCountDownText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        String timeLeftFormatted = String.format("%02d", seconds);
        pauseTimer.setText(timeLeftFormatted);
    }

    /**
     * Wird aufgerufen, wenn die Activity zerstört wird.
     * Bricht den Timer ab, um Ressourcen freizugeben und
     * Memory Leaks zu vermeiden.
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}