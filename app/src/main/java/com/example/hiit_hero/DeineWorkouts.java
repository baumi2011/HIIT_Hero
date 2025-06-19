package com.example.hiit_hero;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

public class DeineWorkouts extends AppCompatActivity {
    public static List<WorkoutSession> eigeneWorkouts = new ArrayList<>();
    private WorkoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_deine_workouts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.your_workouts_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.workoutsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", java.util.Locale.GERMAN);
        adapter = new WorkoutAdapter(eigeneWorkouts, dateFormat);
        recyclerView.setAdapter(adapter);

        // Detailansicht beim Klick
        adapter.setOnItemClickListener(workout -> {
            Intent intent = new Intent(this, WorkoutAnsicht.class);
            intent.putExtra("workoutName", workout.getName());
            intent.putExtra("workoutDuration", workout.getDuration());
            intent.putExtra("workoutDate", dateFormat.format(workout.getDate()));
            intent.putExtra("workoutCalories", workout.getCaloriesBurned());
            intent.putExtra("workoutExercises", workout.getExercisesAsString());
            startActivity(intent);
        });

        // Löschen beim Long-Click
        adapter.setOnItemLongClickListener(position -> {
            new AlertDialog.Builder(DeineWorkouts.this)
                    .setTitle("Workout löschen")
                    .setMessage("Möchtest du dieses Workout wirklich löschen?")
                    .setPositiveButton("Löschen", (dialog, which) -> {
                        eigeneWorkouts.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(DeineWorkouts.this, "Workout gelöscht", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Abbrechen", null)
                    .show();
        });

        TextView emptyWorkoutsText = findViewById(R.id.emptyWorkoutsText);
        if (eigeneWorkouts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyWorkoutsText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyWorkoutsText.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        TextView emptyWorkoutsText = findViewById(R.id.emptyWorkoutsText);
        RecyclerView recyclerView = findViewById(R.id.workoutsRecyclerView);
        if (eigeneWorkouts.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyWorkoutsText.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyWorkoutsText.setVisibility(View.GONE);
        }
    }
}