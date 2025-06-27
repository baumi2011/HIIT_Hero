package com.example.hiit_hero;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import java.util.Calendar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;

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

        Button setReminderTimeButton = findViewById(R.id.setReminderTimeButton);
        TextView reminderTimeText = findViewById(R.id.reminderTimeText);

        // Lade gespeicherte Zeit
        int hour = sharedPreferences.getInt("reminder_hour", 18);
        int minute = sharedPreferences.getInt("reminder_minute", 0);
        updateReminderTimeText(reminderTimeText, hour, minute);

        setReminderTimeButton.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            TimePickerDialog timePicker = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                sharedPreferences.edit()
                        .putInt("reminder_hour", hourOfDay)
                        .putInt("reminder_minute", minute1)
                        .apply();
                updateReminderTimeText(reminderTimeText, hourOfDay, minute1);
                if (notificationsSwitch.isChecked()) {
                    setDailyReminder(hourOfDay, minute1);
                }
            }, hour, minute, true);
            timePicker.show();
        });
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
                int hour = sharedPreferences.getInt("reminder_hour", 18);
                int minute = sharedPreferences.getInt("reminder_minute", 0);
                setDailyReminder(hour, minute);
                Toast.makeText(this, "Benachrichtigungen aktiviert", Toast.LENGTH_SHORT).show();
            } else {
                cancelDailyReminder();
                Toast.makeText(this, "Benachrichtigungen deaktiviert", Toast.LENGTH_SHORT).show();
            }
        });

        achievementsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("achievements_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "Erfolge & Fortschritte aktiviert" : "Erfolge & Fortschritte deaktiviert", Toast.LENGTH_SHORT).show();
        });

        dataCollectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("data_collection_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "Datensammlung aktiviert" : "Datensammlung deaktiviert", Toast.LENGTH_SHORT).show();
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

    private void updateReminderTimeText(TextView textView, int hour, int minute) {
        textView.setText(String.format("Erinnerungszeit: %02d:%02d", hour, minute));
    }

    private void setDailyReminder(int hour, int minute) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && "HIIT_HERO_REMINDER".equals(intent.getAction())) {
            showTrainingNotification();
        }
    }

    private void showTrainingNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "reminder_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Trainingserinnerung", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("HIIT Hero")
                .setContentText("Zeit für dein Training!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        notificationManager.notify(1, builder.build());
    }

    private void cancelDailyReminder() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pendingIntent);
    }
}