package com.example.hiit_hero;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Datenbank.DAO;
import Datenbank.DatenbaseApp;
import Datenbank.User;

/**
 * Activity für die Benutzerprofil-Verwaltung.
 * Diese Activity ermöglicht es dem Benutzer, seine persönlichen Daten
 * wie Name, Alter, Gewicht und Größe zu bearbeiten und zu speichern.
 * Sie lädt vorhandene Daten aus der Datenbank und bietet eine
 * benutzerfreundliche Oberfläche zur Dateneingabe.
 * Die Activity verwendet asynchrone Datenbankoperationen mit einem
 * ExecutorService, um die UI nicht zu blockieren. Sie unterstützt
 * sowohl das Erstellen neuer Benutzerprofile als auch das Aktualisieren
 * bestehender Profile.
 */

public class ProfilActivity extends AppCompatActivity {
    /** Eingabefeld für den Namen */
    private TextInputEditText nameInput;
    /** Eingabefeld für das Alter */
    private TextInputEditText ageInput;
    /** Eingabefeld für das Gewicht */
    private TextInputEditText weightInput;
    /** Eingabefeld für die Größe */
    private TextInputEditText heightInput;
    /** Datenbankinstanz */
    private DatenbaseApp db;
    /** Data Access Object für Benutzeroperationen */
    private DAO userDao;
    /** Executor Service für asynchrone Datenbankoperationen */
    private ExecutorService executorService;
    /** Aktueller Benutzer */
    private User currentUser;

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die Datenbankverbindung, die UI-Elemente und
     * lädt vorhandene Benutzerdaten. Setzt auch Click-Listener
     * für die Speichern- und Abbrechen-Buttons.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // Initialize database
        db = DatenbaseApp.getDatabase(getApplicationContext());
        userDao = db.userDao();
        executorService = Executors.newSingleThreadExecutor();

        // Initialize views
        nameInput = findViewById(R.id.nameInput);
        ageInput = findViewById(R.id.ageInput);
        weightInput = findViewById(R.id.weightInput);
        heightInput = findViewById(R.id.heightInput);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        // Load saved data
        loadSavedData();

        // Set up button click listeners
        saveButton.setOnClickListener(v -> saveData());
        cancelButton.setOnClickListener(v -> finish());
    }

    /**
     * Lädt gespeicherte Benutzerdaten aus der Datenbank.
     * Führt die Datenbankabfrage in einem separaten Thread aus und
     * aktualisiert die UI-Elemente im Hauptthread mit den geladenen
     * Daten. Falls kein Benutzer existiert, bleiben die Felder leer.
     */
    private void loadSavedData() {
        executorService.execute(() -> {
            currentUser = userDao.getFirstUser();
            if (currentUser != null) {
                runOnUiThread(() -> {
                    nameInput.setText(currentUser.name);
                    ageInput.setText(String.valueOf(currentUser.age));
                    weightInput.setText(String.valueOf(currentUser.weight));
                    heightInput.setText(String.valueOf(currentUser.height));
                });
            }
        });
    }

    /**
     * Speichert die eingegebenen Benutzerdaten.
     * Validiert die Eingaben und speichert sie in der Datenbank.
     * Erstellt einen neuen Benutzer, falls noch keiner existiert,
     * oder aktualisiert den bestehenden Benutzer. Zeigt entsprechende
     * Erfolgsmeldungen an und beendet die Activity nach dem Speichern.
     */
    private void saveData() {
        String name = nameInput.getText().toString();
        String ageStr = ageInput.getText().toString();
        String weightStr = weightInput.getText().toString();
        String heightStr = heightInput.getText().toString();

        // Basic validation
        if (name.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            Toast.makeText(this, "Bitte füllen Sie alle Felder aus", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            float weight = Float.parseFloat(weightStr);
            float height = Float.parseFloat(heightStr);

            executorService.execute(() -> {
                if (currentUser == null) {
                    // Neuer Benutzer
                    currentUser = new User();
                    currentUser.name = name;
                    currentUser.age = age;
                    currentUser.weight = weight;
                    currentUser.height = height;
                    userDao.insert(currentUser);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Daten erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    // Existierender Benutzer aktualisieren
                    currentUser.name = name;
                    currentUser.age = age;
                    currentUser.weight = weight;
                    currentUser.height = height;
                    userDao.update(currentUser);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Daten erfolgreich aktualisiert", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Bitte geben Sie gültige Zahlen ein", Toast.LENGTH_SHORT).show();
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