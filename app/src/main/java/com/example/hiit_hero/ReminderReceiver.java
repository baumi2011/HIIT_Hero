package com.example.hiit_hero;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

/**
 * BroadcastReceiver für Trainingserinnerungen.
 * Dieser Receiver wird von AlarmManager aufgerufen, wenn eine geplante
 * Trainingserinnerung ausgelöst werden soll. Er erstellt eine Benachrichtigung,
 * die den Benutzer daran erinnert, sein Training zu absolvieren.
 * Der Receiver erstellt automatisch einen Notification-Channel für Android 8.0+
 * und zeigt eine hochprioritäre Benachrichtigung an, die automatisch
 * geschlossen wird, wenn der Benutzer darauf tippt.
 */

public class ReminderReceiver extends BroadcastReceiver {
    
    /**
     * Wird aufgerufen, wenn der Broadcast empfangen wird.
     * Erstellt eine Benachrichtigung, die den Benutzer an sein Training erinnert.
     * Erstellt einen Notification-Channel für Android 8.0+ und zeigt eine
     * hochprioritäre Benachrichtigung mit dem App-Icon und einer motivierenden
     * Nachricht an.
     * @param context Der Context, in dem der Receiver läuft
     * @param intent Der Intent, der den Broadcast ausgelöst hat
     */

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "reminder_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Trainingserinnerung", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("HIIT Hero")
                .setContentText("Zeit für dein Training!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }
} 