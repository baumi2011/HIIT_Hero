package com.example.hiit_hero;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import com.bumptech.glide.Glide;

/**
 * Activity für die Durchführung einzelner Übungen während eines Workouts.
 * Diese Activity verwaltet die Ausführung einer einzelnen Übung mit einem
 * Countdown-Timer. Sie unterstützt verschiedene Übungstypen und bietet
 * spezielle Funktionalität für Jumping Jacks mit Bewegungserkennung über
 * den Beschleunigungssensor.
 * Die Activity zeigt den Übungsnamen, ein animiertes GIF der Übung und
 * einen Timer an. Sie ermöglicht das Starten, Stoppen und Abbrechen der
 * Übung und navigiert automatisch zur nächsten Übung oder zur Pause.
 */

public class Exercise extends AppCompatActivity implements SensorEventListener {
    /** TextView für die Anzeige des Übungsnamens */
    private TextView exerciseNameText;
    /** TextView für die Anzeige des Countdown-Timers */
    private TextView timerText;
    /** Button zum Starten der Übung */
    private Button startButton;
    /** Button zum Stoppen der Übung */
    private Button stopButton;
    /** Button zum Abbrechen des gesamten Workouts */
    private Button abortButton;
    /** CountDownTimer für die Übungsdauer */
    private CountDownTimer countDownTimer;
    /** Flag, das angibt, ob der Timer läuft */
    private boolean isTimerRunning = false;
    /** Verbleibende Zeit in Millisekunden (Standard: 30 Sekunden) */
    private long timeLeftInMillis = 30000; // 30 Sekunden
    /** Liste aller Übungen im aktuellen Workout */
    private ArrayList<String> exercises;
    /** Index der aktuellen Übung in der Übungsliste */
    private int currentExerciseIndex;
    /** SensorManager für den Zugriff auf Sensoren */
    private SensorManager sensorManager;
    /** Beschleunigungssensor für Bewegungserkennung */
    private Sensor accelerometer;
    /** Flag, das angibt, ob Jumping Jacks aktiv ist */
    private boolean isJumpingJackActive = false;
    /** Flag, das angibt, ob Bewegung erkannt wurde */
    private boolean isMovementDetected = false;
    /** Schwellenwert für die Bewegungserkennung */
    private static final float MOVEMENT_THRESHOLD = 12.0f; // Schwellenwert für Bewegung

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, lädt das entsprechende Übungsbild,
     * konfiguriert die Click-Listener für alle Buttons und setzt bei
     * Jumping Jacks den Bewegungssensor auf. Startet automatisch den
     * Timer, wenn es nicht die erste Übung ist.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise);

        View mainView = findViewById(R.id.main);
        mainView.setBackgroundColor(ContextCompat.getColor(this, R.color.exercise_background));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisiere die Views
        exerciseNameText = findViewById(R.id.exerciseNameText);
        timerText = findViewById(R.id.timerText);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);
        abortButton = findViewById(R.id.abortButton);
        ImageView exerciseImage = findViewById(R.id.exerciseImage);

        // Hole den Übungsnamen aus dem Intent
        String exerciseName = getIntent().getStringExtra("EXERCISE_NAME");
        if (exerciseName != null) {
            exerciseNameText.setText(exerciseName);
            int imageRes = R.drawable.ic_launcher_foreground;
            switch (exerciseName) {
                case "Liegestütze":
                    imageRes = R.drawable.pushups;
                    break;
                case "Plank":
                    imageRes = R.drawable.plank;
                    break;
                case "Jumping Jacks":
                    imageRes = R.drawable.jumpingjacks;
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
            Glide.with(this).asGif().load(imageRes).into(exerciseImage);
            if (exerciseName.equalsIgnoreCase("Jumping Jacks")) {
                Toast.makeText(this, "Bewegungssensor aktiv! Handy in der Hand halten.", Toast.LENGTH_LONG).show();
                isJumpingJackActive = true;
                sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                if (sensorManager != null) {
                    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                }
            }
        }

        // Setze den Click-Listener für den Start-Button
        startButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                if (isJumpingJackActive) {
                    if (sensorManager != null && accelerometer != null) {
                        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                    }
                }
                startTimer();
            }
        });

        // Setze den Click-Listener für den Stop-Button
        stopButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                stopTimer();
                if (isJumpingJackActive && sensorManager != null) {
                    sensorManager.unregisterListener(this);
                }
            }
        });

        // Setze den Click-Listener für den Abbrechen-Button
        abortButton.setOnClickListener(v -> {
            if (isJumpingJackActive && sensorManager != null) {
                sensorManager.unregisterListener(this);
            }
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            Intent intent = new Intent(Exercise.this, WorkoutFertigActivity.class);
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

        updateCountDownText();

        // Hole die Übungsliste und den aktuellen Index aus dem Intent
        exercises = getIntent().getStringArrayListExtra("exercises");
        currentExerciseIndex = getIntent().getIntExtra("currentExerciseIndex", 0);

        // Starte den Timer automatisch, wenn es nicht die erste Übung ist
        if (currentExerciseIndex > 0) {
            if (isJumpingJackActive) {
                if (sensorManager != null && accelerometer != null) {
                    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                }
                // Kein Startbutton nötig, Timer startet bei Bewegung
            } else {
                startTimer();
            }
        }
    }

    /**
     * Startet den Countdown-Timer für die Übung.
     * Bei Jumping Jacks wird der Timer nur gestartet, wenn Bewegung
     * erkannt wird. Der Timer läuft für 5 Sekunden und aktualisiert
     * die Anzeige jede Sekunde. Bei Jumping Jacks wird der Timer
     * pausiert, wenn keine Bewegung mehr erkannt wird.
     */
    private void startTimer() {
        if (isJumpingJackActive) {
            // Timer nur starten, wenn Bewegung erkannt wird
            if (!isMovementDetected) {
                Toast.makeText(this, "Bewege das Handy, um zu starten!", Toast.LENGTH_SHORT).show();
                // Stop-Button trotzdem aktivieren
                stopButton.setEnabled(true);
                return;
            }
        }
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isJumpingJackActive && !isMovementDetected) {
                    // Pausiere den Timer, wenn keine Bewegung erkannt wird
                    cancel();
                    isTimerRunning = false;
                    startButton.setEnabled(true);
                    stopButton.setEnabled(true); // Stop-Button bleibt aktiv
                    Toast.makeText(Exercise.this, "Bewegung gestoppt! Timer pausiert.", Toast.LENGTH_SHORT).show();
                    return;
                }
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                if (isJumpingJackActive && sensorManager != null) {
                    sensorManager.unregisterListener(Exercise.this);
                }
                navigateToPause();
            }
        }.start();
        isTimerRunning = true;
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    /**
     * Stoppt den laufenden Timer.
     * Bricht den CountDownTimer ab und setzt die Button-Zustände zurück.
     * Der Timer kann später wieder gestartet werden.
     */
    private void stopTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    /**
     * Aktualisiert die Timer-Anzeige.
     * Konvertiert die verbleibende Zeit in Sekunden und formatiert
     * sie als zweistellige Zahl für die Anzeige.
     */
    private void updateCountDownText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        String timeLeftFormatted = String.format("%02d", seconds);
        timerText.setText(timeLeftFormatted);
    }

    /**
     * Navigiert zur nächsten Activity nach Abschluss der Übung.
     * Überprüft, ob es weitere Übungen gibt. Wenn es die letzte Übung war,
     * wird zur WorkoutFertigActivity navigiert. Ansonsten wird zur
     * PauseActivity für die Pause zwischen den Übungen navigiert.
     * Alle relevanten Workout-Daten werden an die nächste Activity weitergegeben.
     */
    private void navigateToPause() {
        // Hole die Workout-Werte aus dem Intent
        String workoutName = getIntent().getStringExtra("workoutName");
        String duration = getIntent().getStringExtra("workoutDuration");
        int calories = getIntent().getIntExtra("workoutCalories", 0);
        String exercisesString = String.join(", ", exercises);

        if (exercises != null && currentExerciseIndex + 1 >= exercises.size()) {
            // Wenn es die letzte Übung war, navigiere direkt zur WorkoutFertigActivity
            Intent intent = new Intent(this, WorkoutFertigActivity.class);

            // Übergebe die Workout-Werte
            intent.putExtra("workoutName", workoutName);
            intent.putExtra("workoutDuration", duration);
            intent.putExtra("workoutCalories", calories);
            intent.putExtra("workoutExercises", exercisesString);
            
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // Sonst zur PauseActivity
            Intent intent = new Intent(this, PauseActivity.class);
            intent.putStringArrayListExtra("exercises", exercises);
            intent.putExtra("currentExerciseIndex", currentExerciseIndex);

            // Übergebe die Workout-Werte weiter
            intent.putExtra("workoutName", workoutName);
            intent.putExtra("workoutDuration", duration);
            intent.putExtra("workoutCalories", calories);
            
            startActivity(intent);
            finish();
        }
    }

    /**
     * Wird aufgerufen, wenn die Activity zerstört wird.
     * Bricht den Timer ab und meldet den Sensor-Listener ab,
     * um Ressourcen freizugeben und Memory Leaks zu vermeiden.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (isJumpingJackActive && sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    /**
     * Wird aufgerufen, wenn sich Sensorwerte ändern.
     * Überwacht den Beschleunigungssensor bei Jumping Jacks und erkennt
     * Bewegungen basierend auf dem Schwellenwert. Startet den Timer
     * automatisch, wenn Bewegung erkannt wird.
     * @param event Das SensorEvent mit den aktuellen Sensorwerten
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isJumpingJackActive && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            float acceleration = (float) Math.sqrt(x * x + y * y + z * z);
            if (acceleration > MOVEMENT_THRESHOLD) {
                isMovementDetected = true;
                if (!isTimerRunning) {
                    startTimer();
                }
            } else {
                isMovementDetected = false;
            }
        }
    }

    /**
     * Wird aufgerufen, wenn sich die Genauigkeit des Sensors ändert.
     * Diese Methode ist Teil der SensorEventListener-Schnittstelle,
     * wird aber in dieser Implementierung nicht verwendet.
     * @param sensor Der Sensor, dessen Genauigkeit sich geändert hat
     * @param accuracy Die neue Genauigkeit des Sensors
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nicht benötigt
    }
}