package com.example.hiit_hero;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<WorkoutSession> workouts;
    private SimpleDateFormat dateFormat;

    public WorkoutAdapter(List<WorkoutSession> workouts, SimpleDateFormat dateFormat) {
        this.workouts = workouts;
        this.dateFormat = dateFormat;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        WorkoutSession workout = workouts.get(position);
        holder.nameText.setText(workout.getName());
        holder.durationText.setText(workout.getDuration());
        holder.dateText.setText(dateFormat.format(workout.getTimestamp()));
        holder.caloriesText.setText(workout.getCaloriesBurned() + " kcal");
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView durationText;
        TextView dateText;
        TextView caloriesText;

        WorkoutViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.workoutName);
            durationText = itemView.findViewById(R.id.workoutDuration);
            dateText = itemView.findViewById(R.id.workoutDate);
            caloriesText = itemView.findViewById(R.id.workoutCalories);
        }
    }
}