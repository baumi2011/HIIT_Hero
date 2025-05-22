package com.example.hiit_hero;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

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

        // Set up click listeners for all cards
        MaterialCardView workoutsCard = findViewById(R.id.workoutsCard);
        MaterialCardView progressCard = findViewById(R.id.progressCard);
        MaterialCardView profileCard = findViewById(R.id.profileCard);
        View settingsButton = findViewById(R.id.settingsButton);

        workoutsCard.setOnClickListener(v -> {
            // TODO: Implement WorkoutsActivity
            Toast.makeText(this, "Workouts werden geladen...", Toast.LENGTH_SHORT).show();
        });

        progressCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, FortschritteActivity.class);
            startActivity(intent);
        });

        profileCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfilActivity.class);
            startActivity(intent);
        });

        settingsButton.setOnClickListener(v -> {
            // TODO: Implement SettingsActivity
            Toast.makeText(this, "Einstellungen werden geladen...", Toast.LENGTH_SHORT).show();
        });
    }
}