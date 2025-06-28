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
 * automatisch in der Datenbank gespeichert.
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
    /** Button zum Beenden des Lauf-Workouts */
    private Button stopButton;
    /** Thread für die Timer-Updates */
    private Thread timerThread;
    /** Flag, das angibt, ob das Tracking aktiv ist */
    private boolean isTracking = false;
    /** Startzeit des Workouts in Millisekunden */
    private long startTime;
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
     * und startet das Tracking, falls die Berechtigungen vorhanden sind.
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
        stopButton = findViewById(R.id.stopButton);

        stopButton.setOnClickListener(v -> stopTracking());

        if (checkLocationPermission()) {
            startTracking();
        } else {
            requestLocationPermission();
        }
    }

    /**
     * Startet das GPS-Tracking für das Lauf-Workout.
     * Initialisiert alle Tracking-Variablen, startet den LocationService
     * und registriert den BroadcastReceiver für Positionsupdates.
     * Startet auch einen Timer-Thread für kontinuierliche Zeit-Updates.
     */
    private void startTracking() {
        isTracking = true;
        startTime = SystemClock.uptimeMillis();
        Intent serviceIntent = new Intent(this, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter(LocationService.ACTION_LOCATION_BROADCAST));
        Toast.makeText(this, "Lauf-Workout gestartet!", Toast.LENGTH_SHORT).show();

        // Start a thread to update the timer
        if (timerThread == null) {
            timerThread = new Thread(() -> {
                while (isTracking) {
                    runOnUiThread(() -> {
                        long elapsedMillis = SystemClock.uptimeMillis() - startTime;
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
     * Beendet das GPS-Tracking und speichert das Workout.
     * Stoppt den LocationService, berechnet die finalen Workout-Metriken
     * und speichert das Workout in der Datenbank. Zeigt eine Bestätigungsnachricht
     * an und beendet die Activity.
     */
    private void stopTracking() {
        if (!isTracking) return;
        isTracking = false;
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(locationReceiver);

        if (timerThread != null) {
            timerThread.interrupt();
            timerThread = null;
        }

        long elapsedMillis = SystemClock.uptimeMillis() - startTime;
        String duration = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                (int) (elapsedMillis / 3600000),
                (int) (elapsedMillis / 3600000) / 60000,
                (int) (elapsedMillis / 1000) % 60);

        float avgSpeedKmh = 0;
        if (elapsedMillis > 0) {
            avgSpeedKmh = (totalDistance / 1000) / (elapsedMillis / 3600000.0f);
        }

        // TODO: Kalorienberechnung implementieren
        int caloriesBurnt = (int) (totalDistance / 1000 * 70); // Grobe Schätzung: 70 kcal pro km

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
     * Startet das Tracking, wenn die Berechtigung erteilt wurde, oder
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
                startTracking();
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
            stopTracking();
        }
        super.onDestroy();
    }
} 