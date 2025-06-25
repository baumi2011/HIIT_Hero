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
import android.app.AlertDialog;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import android.view.View;

public class PauseActivity extends AppCompatActivity {
    private TextView pauseTimer;
    private TextView nextExerciseText;
    private Button controlButton;
    private Button abortButton;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 5000; // 5 Sekunden
    private ArrayList<String> exercises;
    private int currentExerciseIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pause);
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
                
                // Wenn es die letzte Übung war, navigiere zur WorkoutFertigActivity
                if (exercises != null && currentExerciseIndex + 1 >= exercises.size()) {
                    Intent intent = new Intent(PauseActivity.this, WorkoutFertigActivity.class);
                    // Übergebe die Workout-Werte
                    String duration = getIntent().getStringExtra("workoutDuration");
                    int calories = getIntent().getIntExtra("workoutCalories", 0);
                    intent.putExtra("workoutDuration", duration);
                    intent.putExtra("workoutCalories", calories);
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
                    String duration = getIntent().getStringExtra("workoutDuration");
                    int calories = getIntent().getIntExtra("workoutCalories", 0);
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

    private void stopTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        controlButton.setText("Start");
    }

    private void updateCountDownText() {
        int seconds = (int) (timeLeftInMillis / 1000);
        String timeLeftFormatted = String.format("%02d", seconds);
        pauseTimer.setText(timeLeftFormatted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}