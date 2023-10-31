package com.example.stickynote;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class NoteStorage {

    private static final String SHARED_PREF_NAME = "MyNotes";
    private static final String KEY_NOTES = "notes";

    public static void saveNotes(Context context, Set<String> notes) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(KEY_NOTES, notes);
        editor.apply();
    }

    public static Set<String> getNotes(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(KEY_NOTES, new HashSet<>());
    }
}
