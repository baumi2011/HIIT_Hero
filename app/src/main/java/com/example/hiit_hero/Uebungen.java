package com.example.hiit_hero;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Activity für die Anzeige aller verfügbaren Übungen.
 * Diese Activity zeigt eine Liste aller in der App verfügbaren Übungen an.
 * Der Benutzer kann auf eine Übung klicken, um zur UebungAnsicht zu navigieren,
 * wo detaillierte Informationen und Anleitungen zur jeweiligen Übung angezeigt werden.
 * Die Activity verwendet eine einfache ListView mit einem ArrayAdapter,
 * um die Übungsnamen anzuzeigen.
 */

public class Uebungen extends AppCompatActivity {
    /** Array mit allen verfügbaren Übungsnamen */
    private final String[] allExercises = new String[]{
            "Liegestütze", "Plank", "Jumping Jacks", "Burpees", "Kniebeugen", "Mountain Climbers"
    };

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, erstellt eine ListView mit allen
     * verfügbaren Übungen und setzt einen Click-Listener, um zur
     * UebungAnsicht zu navigieren, wenn eine Übung ausgewählt wird.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_uebungen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.uebungen_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ListView listView = findViewById(R.id.uebungenListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allExercises);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedExercise = allExercises[position];
                Intent intent = new Intent(Uebungen.this, UebungAnsicht.class);
                intent.putExtra("EXERCISE_NAME", selectedExercise);
                startActivity(intent);
            }
        });
    }
} 