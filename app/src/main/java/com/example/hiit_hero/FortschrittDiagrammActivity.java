package com.example.hiit_hero;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FortschrittDiagrammActivity extends AppCompatActivity {
    private LineChart progressChart;
    private TextView workoutNameText;
    private TextView workoutDateText;
    private TextView workoutCaloriesText;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fortschritt_diagramm);

        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMAN);

        // Initialize views
        progressChart = findViewById(R.id.progressChart);
        workoutNameText = findViewById(R.id.workoutNameText);
        workoutDateText = findViewById(R.id.workoutDateText);
        workoutCaloriesText = findViewById(R.id.workoutCaloriesText);

        setupChart();
        loadWorkoutData();
    }

    private void setupChart() {
        progressChart.getDescription().setEnabled(false);
        progressChart.setTouchEnabled(true);
        progressChart.setDragEnabled(true);
        progressChart.setScaleEnabled(true);
        progressChart.setPinchZoom(true);
        progressChart.setDrawGridBackground(false);

        XAxis xAxis = progressChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        progressChart.getAxisLeft().setDrawGridLines(true);
        progressChart.getAxisRight().setEnabled(false);
        progressChart.getLegend().setEnabled(true);
    }

    private void loadWorkoutData() {
        // TODO: Replace with actual data loading
        List<WorkoutSession> workouts = getDummyWorkouts();
        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < workouts.size(); i++) {
            WorkoutSession workout = workouts.get(i);
            entries.add(new Entry(i, workout.getCaloriesBurned()));
            labels.add(dateFormat.format(workout.getTimestamp()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Verbrannte Kalorien");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        progressChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        progressChart.setData(lineData);
        progressChart.invalidate();

        // Show details of the most recent workout
        if (!workouts.isEmpty()) {
            WorkoutSession latestWorkout = workouts.get(workouts.size() - 1);
            workoutNameText.setText("Workout: " + latestWorkout.getName());
            workoutDateText.setText("Datum: " + dateFormat.format(latestWorkout.getTimestamp()));
            workoutCaloriesText.setText("Verbrannte Kalorien: " + latestWorkout.getCaloriesBurned());
        }
    }

    private List<WorkoutSession> getDummyWorkouts() {
        List<WorkoutSession> workouts = new ArrayList<>();
        // Add dummy data
        workouts.add(new WorkoutSession("HIIT Cardio", "30 Minuten", new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L), 300));
        workouts.add(new WorkoutSession("HIIT Strength", "45 Minuten", new Date(System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000L), 400));
        workouts.add(new WorkoutSession("HIIT Cardio", "30 Minuten", new Date(System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L), 350));
        workouts.add(new WorkoutSession("HIIT Strength", "45 Minuten", new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000L), 450));
        return workouts;
    }
}