package com.example.stickynote;

import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<String> notes;
    private ActionMode actionMode;

    private final SparseBooleanArray selectedItems = new SparseBooleanArray();
    private OnNoteClickListener onNoteClickListener;

    public NoteAdapter(List<String> notes, OnNoteClickListener listener) {
        this.notes = notes;
        this.onNoteClickListener = listener;
    }

    public String getTitleAtPosition(int position) {
        return notes.get(position); // Assuming 'notes' is your list of titles
    }


    public void updateNotes(List<String> filteredNotes) {
        notes.clear();  // Clear the current notes list
        notes.addAll(filteredNotes);  // Add filtered notes to the list
        notifyDataSetChanged();  // Notify the adapter that the data has changed
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(itemView, onNoteClickListener, notes);
    }


    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        String note = notes.get(position);
        boolean isSelected = selectedItems.get(position);
        holder.bindData(note, isSelected);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void toggleSelection(int position) {
        selectedItems.put(position, !selectedItems.get(position));
        notifyItemChanged(position);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> selectedPositions = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            selectedPositions.add(selectedItems.keyAt(i));
        }
        return selectedPositions;
    }

    public void addActionMode(ActionMode mode) {
        actionMode = mode;
    }

    public void addNewNoteAndRefresh(String newNote) {
        notes.add(0, newNote); // Add new note at the beginning of the list
        notifyItemInserted(0);
    }


    public void deleteNoteAtPosition(int position) {
        if (position >= 0 && position < notes.size()) {
            notes.remove(position);
            selectedItems.delete(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            if (onNoteClickListener != null && actionMode != null) {
                int selectedCount = getSelectedItemCount();
                if (selectedCount == 0) {
                    actionMode.finish();
                } else {
                    actionMode.setTitle(String.valueOf(selectedCount));
                }
            }
        }
    }

    public void updateNoteAtPosition(int position, String updatedNote) {
        if (position >= 0 && position < notes.size()) {
            // Update the note content
            notes.set(position, updatedNote);
        }
    }


    public List<String> getNotes() {
        return notes;
    }

    public String getNoteAtPosition(int position) {
        return notes.get(position);
    }

    public interface OnNoteClickListener {
        void onNoteClick(int position);

        void onNoteLongClick(int position);
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView textViewNote;
        private OnNoteClickListener onNoteClickListener;

        public NoteViewHolder(@NonNull View itemView, OnNoteClickListener listener, List<String> notes) {
            super(itemView);
            textViewNote = itemView.findViewById(R.id.textViewNote);
            this.onNoteClickListener = listener;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        public String getTitleAtPosition(int position) {
            if (position >= 0 && position < notes.size()) {
                // Ensure 'notes' is the list containing your note titles
                return notes.get(position); // Replace 'notes' with your actual list of titles
            }
            return null;
        }

        public void bindData(String note, boolean isSelected) {
            textViewNote.setText(note);

            // Adjust TextView height to wrap content
            ViewGroup.LayoutParams layoutParams = textViewNote.getLayoutParams();
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            textViewNote.setLayoutParams(layoutParams);

            setSelected(isSelected);
        }

        public void setSelected(boolean isSelected) {
            float alphaValue = isSelected ? 0.5f : 1f;
            textViewNote.setAlpha(alphaValue);
        }

        @Override
        public void onClick(View v) {
            onNoteClickListener.onNoteClick(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            onNoteClickListener.onNoteLongClick(getAdapterPosition());
            return true;
        }
    }
}