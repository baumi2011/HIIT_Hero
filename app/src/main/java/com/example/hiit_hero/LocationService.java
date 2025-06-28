package com.example.hiit_hero;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * Service für GPS-Tracking während Lauf-Workouts.
 * Dieser Service läuft im Hintergrund und sammelt kontinuierlich GPS-Daten
 * für Lauf-Workouts. Er implementiert LocationListener, um Positionsupdates
 * zu empfangen, und sendet diese über LocalBroadcastManager an die RunningActivity.
 * Der Service läuft als Foreground-Service mit einer Benachrichtigung,
 * um sicherzustellen, dass er nicht vom System beendet wird. Er wird
 * von der RunningActivity gestartet und gestoppt.
 */

public class LocationService extends Service implements LocationListener {
    /** Action für Location-Broadcasts */
    public static final String ACTION_LOCATION_BROADCAST = "com.example.hiit_hero.LOCATION_BROADCAST";
    /** Extra-Key für Location-Daten in Broadcasts */
    public static final String EXTRA_LOCATION = "com.example.hiit_hero.EXTRA_LOCATION";
    /** ID für die Foreground-Service-Benachrichtigung */
    private static final int NOTIFICATION_ID = 1;
    /** ID für den Notification-Channel */
    private static final String CHANNEL_ID = "location_channel";
    /** LocationManager für GPS-Updates */
    private LocationManager locationManager;

    /**
     * Wird beim Erstellen des Services aufgerufen.
     * Initialisiert den LocationManager für GPS-Updates.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * Wird aufgerufen, wenn der Service gestartet wird.
     * Startet den Service als Foreground-Service mit einer Benachrichtigung
     * und beginnt mit dem GPS-Tracking. Fordert GPS-Updates alle 2 Sekunden
     * mit einer Mindestdistanz von 1 Meter an.
     * @param intent Der Intent, der den Service gestartet hat
     * @param flags Flags für den Service-Start
     * @param startId Eindeutige ID für diesen Service-Start
     * @return START_STICKY, damit der Service automatisch neu gestartet wird
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForegroundService();
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
        } catch (SecurityException e) {
            stopSelf();
        }
        return START_STICKY;
    }

    /**
     * Startet den Service als Foreground-Service.
     * Erstellt einen Notification-Channel (für Android 8.0+) und
     * eine Benachrichtigung, um den Service im Vordergrund zu halten.
     * Dies verhindert, dass der Service vom System beendet wird.
     */
    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Location Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Lauf-Tracking aktiv")
                .setContentText("Dein Lauf wird aufgezeichnet.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    /**
     * Wird aufgerufen, wenn sich die GPS-Position ändert.
     * Sendet die neue Position über LocalBroadcastManager an alle
     * registrierten Receiver (z.B. die RunningActivity).
     * @param location Die neue GPS-Position
     */
    @Override
    public void onLocationChanged(Location location) {
        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Wird aufgerufen, wenn der Service zerstört wird.
     * Entfernt alle Location-Updates und beendet den Service ordnungsgemäß.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    /**
     * Wird aufgerufen, wenn ein Client an den Service gebunden wird.
     * Da dieser Service nicht für Binding vorgesehen ist, wird null zurückgegeben.
     * @param intent Der Intent, der die Bindung angefordert hat
     * @return null, da keine Bindung unterstützt wird
     */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Wird aufgerufen, wenn sich der Status des Location-Providers ändert.
     * Diese Methode ist für das LocationListener-Interface erforderlich,
     * wird aber in dieser Implementierung nicht verwendet.
     * @param provider Name des Location-Providers
     * @param status Neuer Status des Providers
     * @param extras Zusätzliche Informationen
     */

    @Override
    public void onStatusChanged(String provider, int status, android.os.Bundle extras) {}

    /**
     * Wird aufgerufen, wenn ein Location-Provider aktiviert wird.
     * Diese Methode ist für das LocationListener-Interface erforderlich,
     * wird aber in dieser Implementierung nicht verwendet.
     * @param provider Name des aktivierten Location-Providers
     */
    @Override
    public void onProviderEnabled(String provider) {}

    /**
     * Wird aufgerufen, wenn ein Location-Provider deaktiviert wird.
     * Diese Methode ist für das LocationListener-Interface erforderlich,
     * wird aber in dieser Implementierung nicht verwendet.
     * @param provider Name des deaktivierten Location-Providers
     */
    @Override
    public void onProviderDisabled(String provider) {}
} 