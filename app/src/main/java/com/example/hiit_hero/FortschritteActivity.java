package com.example.hiit_hero;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import Datenbank.DatenbaseApp;

/**
 * Activity für die Anzeige von Trainingsfortschritten.
 * Diese Activity zeigt detaillierte Statistiken und Fortschritte des
 * Benutzers an, einschließlich wöchentlicher und monatlicher Workout-Listen
 * sowie Liniendiagramme für den Kalorienverbrauch über Zeit.
 * Die Activity verwendet MPAndroidChart für die Visualisierung der Daten
 * und RecyclerViews für die Anzeige der Workout-Listen. Sie filtert
 * Workouts nach Zeiträumen und berechnet aggregierte Statistiken.
 */

public class FortschritteActivity extends AppCompatActivity {
    /** RecyclerView für wöchentliche Workouts */
    private RecyclerView weeklyRecyclerView;
    /** RecyclerView für monatliche Workouts */
    private RecyclerView monthlyRecyclerView;
    /** DateFormat für die Datumsanzeige */
    private SimpleDateFormat dateFormat;
    /** Liniendiagramm für wöchentliche Kalorien */
    private LineChart weeklyChart;
    /** Liniendiagramm für monatliche Kalorien */
    private LineChart monthlyChart;
    /** Datenbankinstanz */
    private DatenbaseApp db;
    /** Executor Service für asynchrone Datenbankoperationen */
    private ExecutorService executorService;

    /**
     * Wird beim Erstellen der Activity aufgerufen.
     * Initialisiert die UI-Elemente, konfiguriert die RecyclerViews
     * und Liniendiagramme, und lädt die Workout-Daten aus der
     * Datenbank für die Fortschrittsanzeige.
     * @param savedInstanceState Bundle mit dem gespeicherten Zustand der Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortschritte);

        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);

        // Initialize RecyclerViews
        weeklyRecyclerView = findViewById(R.id.weeklyRecyclerView);
        monthlyRecyclerView = findViewById(R.id.monthlyRecyclerView);
        TextView weeklyEmptyText = findViewById(R.id.weeklyEmptyText);
        TextView monthlyEmptyText = findViewById(R.id.monthlyEmptyText);
        weeklyChart = findViewById(R.id.weeklyChart);
        monthlyChart = findViewById(R.id.monthlyChart);

        // Set layout managers
        weeklyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        monthlyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Datenbank initialisieren
        db = DatenbaseApp.getDatabase(getApplicationContext());
        executorService = Executors.newSingleThreadExecutor();

        // Workouts aus der Datenbank laden und UI aktualisieren
        loadWorkouts();
    }

    /**
     * Lädt alle Workouts aus der Datenbank.
     * Führt die Datenbankabfrage in einem separaten Thread aus und
     * aktualisiert die UI im Hauptthread mit den geladenen Daten.
     */
    private void loadWorkouts() {
        executorService.execute(() -> {
            List<WorkoutSession> allWorkouts = db.userDao().getAllWorkouts();
            runOnUiThread(() -> {
                updateUiWithWorkouts(allWorkouts);
            });
        });
    }

    /**
     * Aktualisiert die UI mit den geladenen Workout-Daten.
     * Filtert die Workouts nach wöchentlichen und monatlichen Zeiträumen,
     * aktualisiert die RecyclerViews und erstellt die Liniendiagramme
     * für die Kalorienvisualisierung.
     * @param allWorkouts Liste aller Workouts aus der Datenbank
     */
    private void updateUiWithWorkouts(List<WorkoutSession> allWorkouts) {
        List<WorkoutSession> weeklyWorkouts = new ArrayList<>();
        List<WorkoutSession> monthlyWorkouts = new ArrayList<>();
        Date now = new Date();
        long millisInDay = 24 * 60 * 60 * 1000L;

        for (WorkoutSession ws : allWorkouts) {
            long diff = now.getTime() - ws.getDate();
            if (diff <= 7 * millisInDay) {
                weeklyWorkouts.add(ws);
            }
            if (diff <= 30 * millisInDay) {
                monthlyWorkouts.add(ws);
            }
        }

        // Anzeige-Logik für Woche
        if (!weeklyWorkouts.isEmpty()) {
            weeklyRecyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.weeklyEmptyText).setVisibility(View.GONE);
            WorkoutAdapter weeklyAdapter = new WorkoutAdapter(weeklyWorkouts, dateFormat, false);
            weeklyRecyclerView.setAdapter(weeklyAdapter);
        } else {
            weeklyRecyclerView.setVisibility(View.GONE);
            findViewById(R.id.weeklyEmptyText).setVisibility(View.VISIBLE);
        }

        // Anzeige-Logik für Monat
        if (!monthlyWorkouts.isEmpty()) {
            monthlyRecyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.monthlyEmptyText).setVisibility(View.GONE);
            WorkoutAdapter monthlyAdapter = new WorkoutAdapter(monthlyWorkouts, dateFormat, false);
            monthlyRecyclerView.setAdapter(monthlyAdapter);
        } else {
            monthlyRecyclerView.setVisibility(View.GONE);
            findViewById(R.id.monthlyEmptyText).setVisibility(View.VISIBLE);
        }

        // Diagramm für die letzten 7 Tage
        setupChart(weeklyChart, weeklyWorkouts, 7);
        // Diagramm für die letzten 30 Tage
        setupChart(monthlyChart, monthlyWorkouts, 30);
    }

    /**
     * Konfiguriert ein Liniendiagramm für die Kalorienvisualisierung.
     * Erstellt ein Liniendiagramm mit den Kalorienverbrauchsdaten
     * für den angegebenen Zeitraum. Konfiguriert Achsen, Labels
     * und das visuelle Erscheinungsbild des Diagramms.
     * @param chart Das zu konfigurierende Liniendiagramm
     * @param workouts Liste der Workouts für den Zeitraum
     * @param days Anzahl der Tage für die X-Achse
     */
    private void setupChart(LineChart chart, List<WorkoutSession> workouts, int days) {
        if (chart == null) return;
        List<Entry> entries = new ArrayList<>();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd.MM.", Locale.GERMAN);
        Date now = new Date();
        for (int i = days - 1; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DAY_OF_YEAR, -i);
            String dayLabel = dayFormat.format(cal.getTime());
            int totalCalories = 0;
            for (WorkoutSession ws : workouts) {
                if (dayFormat.format(ws.getDate()).equals(dayLabel)) {
                    totalCalories += ws.getCaloriesBurned();
                }
            }
            entries.add(new Entry(days - i - 1, totalCalories));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Kalorien pro Tag");
        dataSet.setColor(ContextCompat.getColor(this, android.R.color.holo_purple));
        dataSet.setValueTextColor(ContextCompat.getColor(this, android.R.color.black));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(days, true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(now);
                cal.add(Calendar.DAY_OF_YEAR, -(days - 1) + (int) value);
                return dayFormat.format(cal.getTime());
            }
        });
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setDrawGridLines(true);
        chart.getAxisRight().setEnabled(false);
        chart.invalidate();
    }
}
