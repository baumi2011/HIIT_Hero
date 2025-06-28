package com.example.hiit_hero;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Datenbank.DAO;
import Datenbank.DatenbaseApp;

/**
 * Hauptaktivität der HIIT Hero App.
 * Diese Activity dient als zentraler Einstiegspunkt der App und zeigt das Hauptmenü
 * mit verschiedenen Navigationsoptionen an. Sie verwaltet die Navigation zu anderen
 * Bereichen der App wie Workouts, Fortschritte, Profil und Einstellungen.
 * Die Activity initialisiert auch die Datenbankverbindung und verwaltet die
 * Sichtbarkeit des Fortschritt-Buttons basierend auf den Benutzereinstellungen.
 */

public class MainActivity extends AppCompatActivity {

    /** Datenbankinstanz für den Zugriff auf die lokale Datenbank */
    private DatenbaseApp db;
    /** Data Access Object für Datenbankoperationen */
    private DAO userDao;
    /** Executor Service für asynchrone Datenbankoperationen */
    private ExecutorService executorService;
    /** Button für die Fortschritte-Ansicht */
    private Button progressButton;

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, setzt die Datenbankverbindung auf,
     * konfiguriert die Click-Listener für alle Navigationsbuttons und
     * verwaltet die Sichtbarkeit des Fortschritt-Buttons basierend auf
     * den Benutzereinstellungen.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // SharedPreferences für Fortschritt-Button
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean achievementsEnabled = sharedPreferences.getBoolean("achievements_enabled", true);

        // Initialisiere Datenbank und DAO
        try {
            db = DatenbaseApp.getDatabase(getApplicationContext());
            userDao = db.userDao();
            executorService = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            Toast.makeText(this, "Datenbankfehler: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up click listeners for all buttons
        Button workoutsButton = findViewById(R.id.workoutsCard);
        progressButton = findViewById(R.id.progressCard);
        Button profileButton = findViewById(R.id.profileCard);
        View settingsButton = findViewById(R.id.settingsButton);

        if (!achievementsEnabled) {
            progressButton.setVisibility(View.GONE);
        }

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

    /**
     * Wird aufgerufen, wenn die Activity wieder sichtbar wird.
     * Überprüft die aktuellen Einstellungen für Achievements und passt
     * die Sichtbarkeit des Fortschritt-Buttons entsprechend an. Dies
     * stellt sicher, dass Änderungen in den Einstellungen sofort
     * sichtbar werden.
     */

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean achievementsEnabled = sharedPreferences.getBoolean("achievements_enabled", true);
        if (progressButton != null) {
            progressButton.setVisibility(achievementsEnabled ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Wird aufgerufen, wenn die Activity zerstört wird.
     * Beendet den ExecutorService ordnungsgemäß, um Ressourcen freizugeben
     * und Memory Leaks zu vermeiden.
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}