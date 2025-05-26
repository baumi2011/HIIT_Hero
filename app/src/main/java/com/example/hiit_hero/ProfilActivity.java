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

public class ProfilActivity extends AppCompatActivity {
    private TextInputEditText nameInput;
    private TextInputEditText ageInput;
    private TextInputEditText weightInput;
    private TextInputEditText heightInput;
    private DatenbaseApp db;
    private DAO userDao;
    private ExecutorService executorService;
    private User currentUser;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}