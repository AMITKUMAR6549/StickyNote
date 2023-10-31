package com.example.stickynote;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddNoteActivity extends AppCompatActivity {

    private EditText editTextNewNote;
    private EditText editTextTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextNewNote = findViewById(R.id.editTextNewNote);
        editTextTitle = findViewById(R.id.EditTextTitle);

        String noteContent = getIntent().getStringExtra(MainActivity.EXTRA_NOTE);
        String noteTitle = getIntent().getStringExtra(MainActivity.EXTRA_TITLE);

        if (noteContent != null) {
            editTextNewNote.setText(noteContent);
            editTextNewNote.setSelection(editTextNewNote.getText().length());

            if (noteTitle != null) {
                editTextTitle.setText(noteTitle);
                editTextTitle.setSelection(editTextTitle.getText().length());
            }
        }

        FloatingActionButton buttonSaveNote = findViewById(R.id.buttonSaveNote);
        buttonSaveNote.setOnClickListener(view -> saveNote());
    }

    private void saveNote() {
        String newNote = editTextNewNote.getText().toString().trim();
        String title = editTextTitle.getText().toString().trim();

        if (TextUtils.isEmpty(newNote)) {
            Toast.makeText(this, "Notes cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(MainActivity.EXTRA_NOTE, newNote);
            resultIntent.putExtra(MainActivity.EXTRA_TITLE, title);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}
