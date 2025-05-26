package com.example.hiit_hero;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up click listeners for all buttons
        Button workoutsButton = findViewById(R.id.workoutsCard);
        Button progressButton = findViewById(R.id.progressCard);
        Button profileButton = findViewById(R.id.profileCard);
        View settingsButton = findViewById(R.id.settingsButton);

        workoutsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Workouts.class);
            startActivity(intent);
        });

        progressButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, FortschritteActivity.class);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfilActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}