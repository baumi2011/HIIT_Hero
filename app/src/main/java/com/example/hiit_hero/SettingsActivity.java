package com.example.hiit_hero;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

        Button backupButton = findViewById(R.id.backupButton);
        Button restoreButton = findViewById(R.id.restoreButton);
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

        // Button listeners
        findViewById(R.id.backupButton).setOnClickListener(v -> {
            // TODO: Implement backup functionality
            Toast.makeText(this, "Backup-Funktion wird implementiert...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.restoreButton).setOnClickListener(v -> {
            // TODO: Implement restore functionality
            Toast.makeText(this, "Wiederherstellungsfunktion wird implementiert...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.aboutButton).setOnClickListener(v -> {
            // TODO: Show about dialog
            Toast.makeText(this, "Über HIIT Hero", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.privacyPolicyButton).setOnClickListener(v -> {
            // TODO: Show privacy policy
            Toast.makeText(this, "Datenschutzerklärung wird angezeigt...", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.termsButton).setOnClickListener(v -> {
            // TODO: Show terms and conditions
            Toast.makeText(this, "Nutzungsbedingungen werden angezeigt...", Toast.LENGTH_SHORT).show();
        });
    }
} 