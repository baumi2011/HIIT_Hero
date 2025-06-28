package com.example.hiit_hero;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.bumptech.glide.Glide;

/**
 * Activity für die detaillierte Ansicht einer einzelnen Übung.
 * Diese Activity zeigt detaillierte Informationen über eine ausgewählte Übung an,
 * einschließlich Beschreibung, betroffene Muskelgruppen, Schwierigkeitsgrad,
 * Kalorienverbrauch und einem animierten GIF der Übung.
 * Die Activity empfängt den Übungsnamen über Intent-Extras und lädt
 * die entsprechenden Informationen und das passende Bild dynamisch.
 */

public class UebungAnsicht extends AppCompatActivity {

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente und lädt die detaillierten Informationen
     * für die ausgewählte Übung. Zeigt Beschreibung, Muskelgruppen,
     * Schwierigkeitsgrad, Kalorienverbrauch und das entsprechende GIF an.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_uebung_ansicht);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.exercise_detail_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView title = findViewById(R.id.exerciseDetailTitle);
        TextView description = findViewById(R.id.exerciseDescription);
        TextView muscleGroups = findViewById(R.id.muscleGroups);
        TextView difficulty = findViewById(R.id.difficultyValue);
        TextView calories = findViewById(R.id.caloriesValue);
        TextView jumpingJackHint = findViewById(R.id.jumpingJackHint);
        jumpingJackHint.setVisibility(View.GONE);

        ImageView image = findViewById(R.id.exerciseImage);

        String exerciseName = getIntent().getStringExtra("EXERCISE_NAME");
        title.setText(exerciseName != null ? exerciseName : "Übung");

        // Beispielhafte Details für jede Übung
        if (exerciseName != null) {
            int imageRes = R.drawable.ic_launcher_foreground;
            switch (exerciseName) {
                case "Liegestütze":
                    description.setText("Klassische Liegestütze für Brust, Schultern und Trizeps");
                    muscleGroups.setText("Brustmuskeln\nTrizeps\nSchultern");
                    difficulty.setText("Mittel");
                    calories.setText("8-10 kcal");
                    imageRes = R.drawable.pushups;
                    break;
                case "Plank":
                    description.setText("Statische Übung für Rumpf und Bauch");
                    muscleGroups.setText("Bauch\nRücken\nSchultern");
                    difficulty.setText("Leicht");
                    calories.setText("4-6 kcal");
                    imageRes = R.drawable.plank;
                    break;
                case "Jumping Jacks":
                    description.setText("Ganzkörperübung zur Aktivierung des Kreislaufs");
                    muscleGroups.setText("Beine\nSchultern\nHerz-Kreislauf");
                    difficulty.setText("Leicht");
                    calories.setText("7-9 kcal");
                    jumpingJackHint.setVisibility(View.VISIBLE);
                    imageRes = R.drawable.jumpingjacks;
                    break;
                case "Burpees":
                    description.setText("Intensive Ganzkörperübung für Kraft und Ausdauer");
                    muscleGroups.setText("Beine\nBrust\nArme\nRumpf");
                    difficulty.setText("Schwer");
                    calories.setText("10-12 kcal");
                    imageRes = R.drawable.burpees;
                    break;
                case "Kniebeugen":
                    description.setText("Klassische Übung für die Beinmuskulatur");
                    muscleGroups.setText("Oberschenkel\nGesäß\nRumpf");
                    difficulty.setText("Leicht");
                    calories.setText("6-8 kcal");
                    imageRes = R.drawable.kniebeugen;
                    break;
                case "Mountain Climbers":
                    description.setText("Dynamische Übung für Bauch und Ausdauer");
                    muscleGroups.setText("Bauch\nBeine\nSchultern");
                    difficulty.setText("Mittel");
                    calories.setText("8-10 kcal");
                    imageRes = R.drawable.mountainclimbers;
                    break;
                default:
                    description.setText("Keine Beschreibung verfügbar");
                    muscleGroups.setText("-");
                    difficulty.setText("-");
                    calories.setText("-");
            }
            Glide.with(this).asGif().load(imageRes).into(image);
        }
    }
}