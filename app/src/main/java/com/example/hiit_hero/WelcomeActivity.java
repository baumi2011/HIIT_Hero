package com.example.hiit_hero;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Willkommens-Activity für neue Benutzer.
 * Diese Activity wird beim ersten Start der App angezeigt und bietet
 * eine Einführung in die wichtigsten Features der HIIT Hero App.
 * Nach dem ersten Besuch wird sie nicht mehr angezeigt, es sei denn,
 * der Benutzer löscht die App-Daten.
 */
public class WelcomeActivity extends AppCompatActivity {

    /** SharedPreferences-Schlüssel für den ersten Start */
    private static final String PREF_NAME = "AppPreferences";
    private static final String KEY_FIRST_LAUNCH = "isFirstLaunch";

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente und setzt den Click-Listener
     * für den "Los geht's!" Button. Wenn der Button geklickt wird,
     * wird das erste Start-Flag gesetzt und zur MainActivity navigiert.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            // Markiere, dass die App bereits gestartet wurde
            markFirstLaunchComplete();
            
            // Navigiere zur MainActivity
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Markiert, dass die App bereits zum ersten Mal gestartet wurde.
     * Speichert in den SharedPreferences, dass die Willkommens-Activity
     * bereits angezeigt wurde, damit sie bei zukünftigen App-Starts
     * übersprungen wird.
     */
    private void markFirstLaunchComplete() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_FIRST_LAUNCH, false);
        editor.apply();
    }

    /**
     * Überprüft, ob es der erste Start der App ist.
     * Diese statische Methode kann von anderen Activities verwendet werden,
     * um zu entscheiden, ob die WelcomeActivity angezeigt werden soll.
     * @param context Der Application-Context
     * @return true, wenn es der erste Start ist, sonst false
     */
    public static boolean isFirstLaunch(android.content.Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true);
    }
} 