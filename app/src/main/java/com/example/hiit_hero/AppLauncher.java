package com.example.hiit_hero;

import android.content.Context;
import android.content.Intent;

/**
 * Hilfsklasse für die App-Start-Logik.
 * Diese Klasse verwaltet die Entscheidung, welche Activity beim App-Start
 * angezeigt werden soll. Sie prüft, ob es der erste Start der App ist
 * und leitet entsprechend zur WelcomeActivity oder MainActivity weiter.
 */
public class AppLauncher {

    /**
     * Bestimmt die richtige Start-Activity und startet sie.
     * Diese Methode prüft, ob es der erste Start der App ist.
     * Falls ja, wird die WelcomeActivity gestartet, ansonsten
     * die MainActivity.
     * @param context Der Application-Context
     */
    public static void launchApp(Context context) {
        Intent intent;
        
        if (WelcomeActivity.isFirstLaunch(context)) {
            // Erster Start - zeige WelcomeActivity
            intent = new Intent(context, WelcomeActivity.class);
        } else {
            // Nicht der erste Start - zeige MainActivity
            intent = new Intent(context, MainActivity.class);
        }
        
        // Setze Flags für sauberen App-Start
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /**
     * Setzt das erste Start-Flag zurück.
     * Diese Methode kann verwendet werden, um die WelcomeActivity
     * erneut anzuzeigen (z.B. für Testzwecke oder nach App-Reset).
     * @param context Der Application-Context
     */

    public static void resetFirstLaunch(Context context) {
        android.content.SharedPreferences sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isFirstLaunch", true);
        editor.apply();
    }
} 