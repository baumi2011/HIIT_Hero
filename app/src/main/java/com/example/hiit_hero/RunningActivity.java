package com.example.hiit_hero;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Activity für Lauf-Workouts mit GPS-Tracking.
 * Diese Activity ermöglicht es dem Benutzer, Lauf-Workouts durchzuführen
 * und dabei verschiedene Metriken wie Zeit, Distanz und Geschwindigkeit
 * zu verfolgen. Sie verwendet den LocationService für die GPS-Ortung
 * und speichert die Workout-Daten in der lokalen Datenbank.
 * Die Activity zeigt Echtzeit-Updates der Lauf-Metriken an und kann
 * das Workout jederzeit beenden. Nach Abschluss wird das Workout
 * automatisch in der lokalen Datenbank gespeichert.
 */

public class RunningActivity extends AppCompatActivity {

    /** Request-Code für Standortberechtigungen */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    /** TextView für die Anzeige der Laufzeit */
    private TextView timeValue;
    /** TextView für die Anzeige der zurückgelegten Distanz */
    private TextView distanceValue;
    /** TextView für die Anzeige der aktuellen Geschwindigkeit */
    private TextView speedValue;
    /** Button zum Starten des Lauf-Workouts */
    private Button startButton;
    /** Button zum Pausieren/Fortsetzen des Lauf-Workouts */
    private Button pauseButton;
    /** Button zum Beenden des Lauf-Workouts */
    private Button finishButton;
    /** Thread für die Timer-Updates */
    private Thread timerThread;
    /** Flag, das angibt, ob das Tracking aktiv ist */
    private boolean isTracking = false;
    /** Flag, das angibt, ob das Workout pausiert ist */
    private boolean isPaused = false;
    /** Startzeit des Workouts in Millisekunden */
    private long startTime;
    /** Gesamte Pausenzeit in Millisekunden */
    private long totalPauseTime = 0;
    /** Zeitpunkt, an dem das Workout pausiert wurde */
    private long pauseStartTime;
    /** Gesamte zurückgelegte Distanz in Metern */
    private float totalDistance = 0;
    /** Letzte bekannte GPS-Position */
    private Location lastLocation;
    /** Liste aller GPS-Positionen während des Laufs */
    private ArrayList<Location> path = new ArrayList<>();

    /**
     * BroadcastReceiver für GPS-Positionsupdates vom LocationService.
     * Empfängt kontinuierliche Updates der GPS-Position und aktualisiert
     * die UI entsprechend.
     */
    private BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(LocationService.EXTRA_LOCATION);
            if (location != null) {
                updateUI(location);
            }
        }
    };

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, überprüft die Standortberechtigungen
     * und bereitet die Buttons für das Workout vor.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_running);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        timeValue = findViewById(R.id.timeValue);
        distanceValue = findViewById(R.id.distanceValue);
        speedValue = findViewById(R.id.speedValue);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        finishButton = findViewById(R.id.finishButton);

        // Setze Click-Listener für alle Buttons
        startButton.setOnClickListener(v -> startWorkout());
        pauseButton.setOnClickListener(v -> togglePause());
        finishButton.setOnClickListener(v -> finishWorkout());

        // Überprüfe Standortberechtigungen
        if (!checkLocationPermission()) {
            requestLocationPermission();
        }
    }

    /**
     * Startet das Lauf-Workout.
     * Initialisiert alle Tracking-Variablen, startet den LocationService
     * und registriert den BroadcastReceiver für Positionsupdates.
     * Startet auch einen Timer-Thread für kontinuierliche Zeit-Updates.
     */
    private void startWorkout() {
        if (isTracking) return;
        
        isTracking = true;
        isPaused = false;
        startTime = SystemClock.uptimeMillis();
        totalPauseTime = 0;
        
        Intent serviceIntent = new Intent(this, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST));
        
        // Aktualisiere Button-States
        startButton.setEnabled(false);
        pauseButton.setEnabled(true);
        finishButton.setEnabled(true);
        
        Toast.makeText(this, "Lauf-Workout gestartet!", Toast.LENGTH_SHORT).show();

        // Starte Timer-Thread
        if (timerThread == null) {
            timerThread = new Thread(() -> {
                while (isTracking) {
                    runOnUiThread(() -> {
                        long elapsedMillis;
                        if (!isPaused) {
                            elapsedMillis = SystemClock.uptimeMillis() - startTime - totalPauseTime;
                        } else {
                            elapsedMillis = pauseStartTime - startTime - totalPauseTime;
                        }
                        int hours = (int) (elapsedMillis / 3600000);
                        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
                        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
                        timeValue.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            timerThread.start();
        }
    }

    /**
     * Pausiert oder setzt das Workout fort.
     * Stoppt oder startet den LocationService entsprechend und
     * aktualisiert die Button-States und Timer-Berechnung.
     */
    private void togglePause() {
        if (!isTracking) return;
        
        if (isPaused) {
            // Workout fortsetzen
            isPaused = false;
            totalPauseTime += SystemClock.uptimeMillis() - pauseStartTime;
            
            Intent serviceIntent = new Intent(this, LocationService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
            LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST));
            
            pauseButton.setText("Pausieren");
            Toast.makeText(this, "Workout fortgesetzt!", Toast.LENGTH_SHORT).show();
        } else {
            // Workout pausieren
            isPaused = true;
            pauseStartTime = SystemClock.uptimeMillis();
            
            Intent serviceIntent = new Intent(this, LocationService.class);
            stopService(serviceIntent);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);
            
            pauseButton.setText("Fortsetzen");
            Toast.makeText(this, "Workout pausiert!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Beendet das Lauf-Workout und speichert es in der Datenbank.
     * Stoppt den LocationService, berechnet die finalen Workout-Metriken
     * und speichert das Workout in der Datenbank. Zeigt eine Bestätigungsnachricht
     * an und beendet die Activity.
     */
    private void finishWorkout() {
        if (!isTracking) return;
        
        isTracking = false;
        isPaused = false;
        
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);

        if (timerThread != null) {
            timerThread.interrupt();
            timerThread = null;
        }

        // Berechne finale Workout-Metriken
        long elapsedMillis = SystemClock.uptimeMillis() - startTime - totalPauseTime;
        String duration = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                (int) (elapsedMillis / 3600000),
                (int) (elapsedMillis / 60000) % 60,
                (int) (elapsedMillis / 1000) % 60);

        float avgSpeedKmh = 0;
        if (elapsedMillis > 0) {
            avgSpeedKmh = (totalDistance / 1000) / (elapsedMillis / 3600000.0f);
        }

        // Kalorienberechnung (grobe Schätzung: 70 kcal pro km)
        int caloriesBurnt = (int) (totalDistance / 1000 * 70);

        WorkoutSession session = new WorkoutSession("Lauf-Workout", duration, System.currentTimeMillis(), caloriesBurnt, totalDistance / 1000, avgSpeedKmh);

        new Thread(() -> {
            Datenbank.DatenbaseApp.getDatabase(getApplicationContext()).userDao().insertWorkout(session);
        }).start();

        Toast.makeText(this, "Workout gespeichert!", Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Aktualisiert die UI mit neuen GPS-Daten.
     * Berechnet die zurückgelegte Distanz basierend auf der neuen Position,
     * aktualisiert die Gesamtdistanz und zeigt die aktuellen Werte in der UI an.
     * @param location Die neue GPS-Position
     */
    private void updateUI(Location location) {
        if (lastLocation != null) {
            totalDistance += lastLocation.distanceTo(location);
        }
        lastLocation = location;
        path.add(location);

        float speedKmh = location.getSpeed() * 3.6f;

        distanceValue.setText(String.format(Locale.getDefault(), "%.2f km", totalDistance / 1000));
        speedValue.setText(String.format(Locale.getDefault(), "%.1f km/h", speedKmh));
    }

    /**
     * Überprüft, ob die Standortberechtigung erteilt wurde.
     * @return true, wenn die Standortberechtigung erteilt wurde, sonst false
     */
    private boolean checkLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Fordert die Standortberechtigung an.
     * Zeigt dem Benutzer den Berechtigungsdialog an.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    /**
     * Wird aufgerufen, wenn der Benutzer auf die Berechtigungsanfrage antwortet.
     * Aktiviert den Start-Button, wenn die Berechtigung erteilt wurde, oder
     * beendet die Activity mit einer Fehlermeldung.
     * @param requestCode Der Request-Code der Berechtigungsanfrage
     * @param permissions Array der angeforderten Berechtigungen
     * @param grantResults Array der Berechtigungsergebnisse
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Standortberechtigung erteilt. Du kannst jetzt das Workout starten.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Standortberechtigung verweigert. Workout kann nicht gestartet werden.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /**
     * Wird aufgerufen, wenn die Activity zerstört wird.
     * Stellt sicher, dass das Tracking gestoppt wird, falls die Activity
     * unerwartet beendet wird.
     */

    @Override
    protected void onDestroy() {
        // Ensure tracking stops if the activity is destroyed
        if (isTracking) {
            finishWorkout();
        }
        super.onDestroy();
    }
} 