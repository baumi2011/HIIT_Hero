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

public class Exercise extends AppCompatActivity {
    private TextView exerciseNameText;
    private TextView timerText;
    private Button startButton;
    private Button stopButton;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 5000; // 5 Sekunden
    private ArrayList<String> exercises;
    private int currentExerciseIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise);
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

        // Hole den Übungsnamen aus dem Intent
        String exerciseName = getIntent().getStringExtra("EXERCISE_NAME");
        if (exerciseName != null) {
            exerciseNameText.setText(exerciseName);
        }

        // Setze den Click-Listener für den Start-Button
        startButton.setOnClickListener(v -> {
            if (!isTimerRunning) {
                startTimer();
            }
        });

        // Setze den Click-Listener für den Stop-Button
        stopButton.setOnClickListener(v -> {
            if (isTimerRunning) {
                stopTimer();
            }
        });

        updateCountDownText();

        // Hole die Übungsliste und den aktuellen Index aus dem Intent
        exercises = getIntent().getStringArrayListExtra("exercises");
        currentExerciseIndex = getIntent().getIntExtra("currentExerciseIndex", 0);

        // Starte den Timer automatisch, wenn es nicht die erste Übung ist
        if (currentExerciseIndex > 0) {
            startTimer();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
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
        if (exercises != null && currentExerciseIndex + 1 >= exercises.size()) {
            // Wenn es die letzte Übung war, navigiere direkt zur WorkoutFertigActivity
            Intent intent = new Intent(this, WorkoutFertigActivity.class);
            
            // Hole die Workout-Werte aus dem Intent
            String duration = getIntent().getStringExtra("workoutDuration");
            int calories = getIntent().getIntExtra("workoutCalories", 0);
            
            // Übergebe die Workout-Werte
            intent.putExtra("workoutDuration", duration);
            intent.putExtra("workoutCalories", calories);
            
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
            String duration = getIntent().getStringExtra("workoutDuration");
            int calories = getIntent().getIntExtra("workoutCalories", 0);
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
    }
}