package com.example.hiit_hero;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class ProfilActivity extends AppCompatActivity {
    private TextInputEditText nameInput;
    private TextInputEditText ageInput;
    private TextInputEditText weightInput;
    private TextInputEditText heightInput;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);

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
        nameInput.setText(sharedPreferences.getString("name", ""));
        ageInput.setText(sharedPreferences.getString("age", ""));
        weightInput.setText(sharedPreferences.getString("weight", ""));
        heightInput.setText(sharedPreferences.getString("height", ""));
    }

    private void saveData() {
        String name = nameInput.getText().toString();
        String age = ageInput.getText().toString();
        String weight = weightInput.getText().toString();
        String height = heightInput.getText().toString();

        // Basic validation
        if (name.isEmpty() || age.isEmpty() || weight.isEmpty() || height.isEmpty()) {
            Toast.makeText(this, "Bitte füllen Sie alle Felder aus", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("age", age);
        editor.putString("weight", weight);
        editor.putString("height", height);
        editor.apply();

        Toast.makeText(this, "Daten erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
        finish();
    }
}