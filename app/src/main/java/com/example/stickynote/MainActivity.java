package com.example.stickynote;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteAdapter.OnNoteClickListener {

    // Constants for Intent extras and request codes
    public static final String EXTRA_NOTE = "extra_note";
    public static final String EXTRA_TITLE = "extra_title";
    private static final int REQUEST_CODE_ADD_NOTE = 1;
    private static final int REQUEST_CODE_EDIT_NOTE = 2;

    private EditText editTextSearch;
    private NoteAdapter noteAdapter;
    private ActionMode actionMode;
    private ActionModeCallback actionModeCallback;

    private static final String SHARED_PREF_NAME = "MyNotes";
    private static final String KEY_NOTES = "notes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        editTextSearch = findViewById(R.id.editTextSearch);
        noteAdapter = new NoteAdapter(getSavedNotes(), this);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(noteAdapter);

        // Set up search functionality
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String searchQuery = charSequence.toString().toLowerCase();
                List<String> filteredNotes = filterNotes(searchQuery);
                noteAdapter.updateNotes(filteredNotes);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Set up FloatingActionButton click listener
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
        });

        // Set up ActionMode and its callback
        actionModeCallback = new ActionModeCallback();
    }


    // Filter notes based on the search query
    private List<String> filterNotes(String query) {
        List<String> filteredNotes = new ArrayList<>();
        for (String note : getSavedNotes()) {
            if (note.toLowerCase().contains(query)) {
                filteredNotes.add(note);
            }
        }
        return filteredNotes;
    }

    @Override
    public void onNoteClick(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            // Handle single note click (if action mode is not active)
            String clickedNote = noteAdapter.getNoteAtPosition(position);

            // Create an Intent to open AddNoteActivity
            Intent intent = new Intent(MainActivity.this, AddNoteActivity.class);

            // Pass the clicked note content and its position to AddNoteActivity
            intent.putExtra(EXTRA_NOTE, clickedNote);
            intent.putExtra(EXTRA_TITLE, noteAdapter.getTitleAtPosition(position));

            // Start AddNoteActivity with the request code for editing
            startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
        }
    }

    @Override
    public void onNoteLongClick(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        noteAdapter.toggleSelection(position);
        int count = noteAdapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements androidx.appcompat.view.ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(androidx.appcompat.view.ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(androidx.appcompat.view.ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.menu_delete) {
                List<Integer> selectedItems = noteAdapter.getSelectedItems();
                for (int i = selectedItems.size() - 1; i >= 0; i--) {
                    noteAdapter.deleteNoteAtPosition(selectedItems.get(i));
                }
                saveNotesToSharedPreferences(noteAdapter.getNotes());
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(androidx.appcompat.view.ActionMode mode) {
            noteAdapter.clearSelection();
            actionMode = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK && data != null) {
            if (data.hasExtra(EXTRA_NOTE) && data.hasExtra(EXTRA_TITLE)) {
                String newNote = data.getStringExtra(EXTRA_NOTE);
                String title = data.getStringExtra(EXTRA_TITLE);

                if (newNote != null && !newNote.isEmpty()) {
                    // Set the title at the top of the screen
                    setTitle(TextUtils.isEmpty(title) ? "Untitled" : title);
                    newNote = TextUtils.isEmpty(title) ? "\n" + newNote : title + "\n\n" + newNote;

                    // Add the new note to the adapter and refresh the UI
                    noteAdapter.addNewNoteAndRefresh(newNote);

                    // Save the notes including titles
                    List<String> updatedNotes = noteAdapter.getNotes();
                    saveNotesToSharedPreferences(updatedNotes);
                } else {
                    Toast.makeText(this, "Notes cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveNotesToSharedPreferences(List<String> notes) {
        NoteStorage.saveNotes(this, new HashSet<>(notes));
    }

    private List<String> getSavedNotes() {
        return new ArrayList<>(NoteStorage.getNotes(this));
    }
}
