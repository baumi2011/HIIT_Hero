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

/**
 * RecyclerView-Adapter für die Anzeige von Workout-Sessions.
 * Dieser Adapter verwaltet die Anzeige von Workout-Sessions in einer RecyclerView.
 * Er unterstützt verschiedene Interaktionen wie Klicks, Long-Clicks und das Löschen
 * von Workouts. Der Adapter kann zwischen normalen Workout-Listen und benutzerdefinierten
 * Workout-Listen unterscheiden und zeigt entsprechend unterschiedliche UI-Elemente an.
 */
public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {
    /** Liste der Workout-Sessions, die angezeigt werden sollen */
    private List<WorkoutSession> workouts;
    /** DateFormat für die Formatierung der Datumsanzeige */
    private SimpleDateFormat dateFormat;
    /** Listener für normale Klicks auf Workout-Items */
    private OnItemClickListener listener;
    /** Listener für Lösch-Aktionen */
    private OnDeleteClickListener deleteListener;
    /** Flag, das angibt, ob es sich um eine benutzerdefinierte Workout-Liste handelt */
    private boolean isCustomWorkoutList;
    /** Flag, das angibt, ob ein Long-Click aktiv ist */
    private boolean longClickActive = false;

    /**
     * Interface für Click-Events auf Workout-Items.
     */
    public interface OnItemClickListener {

        /**
         * Wird aufgerufen, wenn auf ein Workout-Item geklickt wird.
         * @param workout Die Workout-Session, auf die geklickt wurde
         */
        void onItemClick(WorkoutSession workout);
    }

    /**
     * Interface für Long-Click-Events auf Workout-Items.
     */
    public interface OnItemLongClickListener {
        /**
         * Wird aufgerufen, wenn ein Workout-Item lange gedrückt wird.
         * @param position Die Position des Items in der Liste
         */
        void onItemLongClick(int position);
    }

    /** Listener für Long-Click-Events */
    private OnItemLongClickListener longClickListener;

    /**
     * Setzt den Long-Click-Listener.
     * @param listener Der Listener für Long-Click-Events
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    /**
     * Interface für Lösch-Events auf Workout-Items.
     */
    public interface OnDeleteClickListener {
        /**
         * Wird aufgerufen, wenn der Lösch-Button eines Workouts geklickt wird.
         * @param workout Die Workout-Session, die gelöscht werden soll
         */
        void onDeleteClick(WorkoutSession workout);
    }

    /**
     * Konstruktor für den WorkoutAdapter.
     * @param workouts Liste der Workout-Sessions
     * @param dateFormat DateFormat für die Datumsanzeige
     * @param isCustomWorkoutList Flag, das angibt, ob es sich um benutzerdefinierte Workouts handelt
     */
    public WorkoutAdapter(List<WorkoutSession> workouts, SimpleDateFormat dateFormat, boolean isCustomWorkoutList) {
        this.workouts = workouts;
        this.dateFormat = dateFormat;
        this.isCustomWorkoutList = isCustomWorkoutList;
    }

    /**
     * Setzt den Click-Listener für Workout-Items.
     * @param listener Der Listener für Click-Events
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * Setzt den Lösch-Listener für Workout-Items.
     * @param listener Der Listener für Lösch-Events
     */
    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        this.deleteListener = listener;
    }

    /**
     * Erstellt einen neuen ViewHolder für Workout-Items.
     * @param parent Die ViewGroup, in die der neue View eingefügt wird
     * @param viewType Der View-Typ des neuen Views
     * @return Ein neuer WorkoutViewHolder
     */

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout, parent, false);
        return new WorkoutViewHolder(view);
    }

    /**
     * Bindet die Daten einer Workout-Session an einen ViewHolder.
     * Zeigt die Workout-Informationen an und konfiguriert die Click-Listener
     * basierend auf dem Typ der Workout-Liste.
     * @param holder Der ViewHolder, an den die Daten gebunden werden
     * @param position Die Position des Items in der Liste
     */

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

    /**
     * Gibt die Anzahl der Items in der Liste zurück.
     * @return Anzahl der Workout-Sessions
     */

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    /**
     * Entfernt eine Workout-Session aus der Liste.
     * Entfernt das Workout aus der internen Liste und benachrichtigt
     * den Adapter über die Änderung.
     * @param workout Die Workout-Session, die entfernt werden soll
     */
    public void removeWorkout(WorkoutSession workout) {
        int position = workouts.indexOf(workout);
        if (position != -1) {
            workouts.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * ViewHolder-Klasse für Workout-Items.
     * Hält Referenzen auf die UI-Elemente eines einzelnen Workout-Items
     * in der RecyclerView.
     */
    static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        /** TextView für den Workout-Namen */
        TextView nameText;
        /** TextView für die Workout-Dauer */
        TextView durationText;
        /** TextView für das Workout-Datum */
        TextView dateText;
        /** TextView für die verbrannten Kalorien */
        TextView caloriesText;
        /** Button zum Löschen des Workouts */
        ImageButton deleteButton;

        /**
         * Konstruktor für den WorkoutViewHolder.
         * Initialisiert die Referenzen auf die UI-Elemente.
         * @param itemView Die View des Workout-Items
         */
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