package com.example.hiit_hero;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    private List<WorkoutSession> workouts;
    private SimpleDateFormat dateFormat;
    private OnItemClickListener listener;
    private OnDeleteClickListener deleteListener;
    private boolean isCustomWorkoutList;
    private boolean longClickActive = false;
    public interface OnItemClickListener {
        void onItemClick(WorkoutSession workout);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }
    private OnItemLongClickListener longClickListener;
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(WorkoutSession workout);
    }

    public WorkoutAdapter(List<WorkoutSession> workouts, SimpleDateFormat dateFormat, boolean isCustomWorkoutList) {
        this.workouts = workouts;
        this.dateFormat = dateFormat;
        this.isCustomWorkoutList = isCustomWorkoutList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
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
        holder.dateText.setText(dateFormat.format(workout.getDate()));
        holder.caloriesText.setText(workout.getCaloriesBurned() + " kcal");

        // Zeige den Lösch-Button nur bei benutzerdefinierten Workouts
        if (isCustomWorkoutList) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteClick(workout);
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }

        // Setze den Click-Listener für das Item
        holder.itemView.setOnClickListener(v -> {
            if (longClickActive) {
                longClickActive = false;
                return;
            }
            if (listener != null) {
                listener.onItemClick(workout);
            }
        });
        // Setze den LongClick-Listener für das Item
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickActive = true;
                longClickListener.onItemLongClick(position);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    public void removeWorkout(WorkoutSession workout) {
        int position = workouts.indexOf(workout);
        if (position != -1) {
            workouts.remove(position);
            notifyItemRemoved(position);
        }
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView durationText;
        TextView dateText;
        TextView caloriesText;
        ImageButton deleteButton;

        WorkoutViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.workoutName);
            durationText = itemView.findViewById(R.id.workoutDuration);
            dateText = itemView.findViewById(R.id.workoutDate);
            caloriesText = itemView.findViewById(R.id.workoutCalories);
            deleteButton = itemView.findViewById(R.id.deleteWorkout);
        }
    }
}