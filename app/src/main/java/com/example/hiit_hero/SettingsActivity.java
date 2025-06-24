package com.example.hiit_hero;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private Switch notificationsSwitch;
    private Switch achievementsSwitch;
    private Switch dataCollectionSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);

        // Initialize views
        initializeViews();
        loadSettings();
        setupListeners();
    }

    private void initializeViews() {
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        achievementsSwitch = findViewById(R.id.achievementsSwitch);
        dataCollectionSwitch = findViewById(R.id.dataCollectionSwitch);

        Button aboutButton = findViewById(R.id.aboutButton);
        Button privacyPolicyButton = findViewById(R.id.privacyPolicyButton);
        Button termsButton = findViewById(R.id.termsButton);
    }

    private void loadSettings() {
        notificationsSwitch.setChecked(sharedPreferences.getBoolean("notifications_enabled", true));
        achievementsSwitch.setChecked(sharedPreferences.getBoolean("achievements_enabled", true));
        dataCollectionSwitch.setChecked(sharedPreferences.getBoolean("data_collection_enabled", false));
    }

    private void setupListeners() {
        // Switch listeners
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply();
            if (isChecked) {
                // TODO: Implement notification scheduling
                Toast.makeText(this, "Benachrichtigungen aktiviert", Toast.LENGTH_SHORT).show();
            } else {
                // TODO: Cancel scheduled notifications
                Toast.makeText(this, "Benachrichtigungen deaktiviert", Toast.LENGTH_SHORT).show();
            }
        });

        achievementsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("achievements_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "Erfolgsbenachrichtigungen aktiviert" : "Erfolgsbenachrichtigungen deaktiviert", 
                    Toast.LENGTH_SHORT).show();
        });

        dataCollectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("data_collection_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "Datensammlung aktiviert" : "Datensammlung deaktiviert", 
                    Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.aboutButton).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Über HIIT Hero")
                .setMessage("HIIT Hero ist deine persönliche Fitness-App für effektives High-Intensity Intervall Training (HIIT). Egal ob Anfänger oder Profi – mit HIIT Hero kannst du individuelle Workouts erstellen, deine Fortschritte verfolgen und dich immer wieder neu motivieren. Unser Ziel ist es, dir ein abwechslungsreiches, motivierendes und gesundes Trainingserlebnis zu bieten. Viel Spaß beim Schwitzen und Erreichen deiner Fitnessziele!")
                .setPositiveButton("OK", null)
                .show();
        });

        findViewById(R.id.privacyPolicyButton).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Datenschutzerklärung")
                .setMessage("Der Schutz deiner Daten ist uns sehr wichtig. HIIT Hero speichert deine Trainingsdaten ausschließlich lokal auf deinem Gerät. Es werden keine persönlichen Daten an Dritte weitergegeben oder auf externe Server übertragen. Wir verwenden keine Tracker oder Werbenetzwerke. Du hast jederzeit die Kontrolle über deine Daten und kannst sie auf Wunsch löschen. Bei Fragen zum Datenschutz kannst du uns jederzeit kontaktieren.")
                .setPositiveButton("OK", null)
                .show();
        });

        findViewById(R.id.termsButton).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Nutzungsbedingungen")
                .setMessage("Mit der Nutzung von HIIT Hero erklärst du dich damit einverstanden, die App ausschließlich für private, nicht-kommerzielle Zwecke zu verwenden. Die bereitgestellten Trainingspläne und Inhalte dienen ausschließlich der Information und Motivation. Die Nutzung erfolgt auf eigene Verantwortung. Bei gesundheitlichen Bedenken konsultiere bitte vor Trainingsbeginn einen Arzt. Wir übernehmen keine Haftung für Verletzungen oder Schäden, die durch die Nutzung der App entstehen.")
                .setPositiveButton("OK", null)
                .show();
        });
    }
} 