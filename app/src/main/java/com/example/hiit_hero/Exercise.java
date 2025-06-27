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

public class Exercise extends AppCompatActivity implements SensorEventListener {
    private TextView exerciseNameText;
    private TextView timerText;
    private Button startButton;
    private Button stopButton;
    private Button abortButton;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 5000; // 5 Sekunden
    private ArrayList<String> exercises;
    private int currentExerciseIndex;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isJumpingJackActive = false;
    private boolean isMovementDetected = false;
    private static final float MOVEMENT_THRESHOLD = 12.0f; // Schwellenwert für Bewegung

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

    private void stopTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void updateCountDownText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        String timeLeftFormatted = String.format("%02d", seconds);
        timerText.setText(timeLeftFormatted);
    }

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

    // SensorEventListener-Methoden
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
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nicht benötigt
    }
}