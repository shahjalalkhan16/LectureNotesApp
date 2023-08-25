package com.example.lecturenotesapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class NoteEditorActivity extends AppCompatActivity {

    EditText lectureTitleEditText; // Declare the EditText
    Button saveButton; // Declare the Button

    int noteId;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        lectureTitleEditText = findViewById(R.id.lectureTitleEditText); // Initialize the EditText
        saveButton = findViewById(R.id.saveButton); // Initialize the Button
        editText = findViewById(R.id.editText);

        // Fetch data that is passed from MainActivity
        Intent intent = getIntent();

        // Accessing the data using key and value
        noteId = intent.getIntExtra("noteId", -1);
//        if (noteId != -1) {
//            editText.setText(MainActivity.notes.get(noteId));
//        } else {
//            MainActivity.notes.add("");
//            noteId = MainActivity.notes.size() - 1;
//            MainActivity.arrayAdapter.notifyDataSetChanged();
//        }

        /*if (noteId != -1) {
            // Retrieve the note content and lecture title
            String[] noteData = MainActivity.notes.get(noteId).split("\n", 2);
            lectureTitleEditText.setText(noteData[0]);
            editText.setText(noteData[1]);
        } else {
            MainActivity.notes.add("\n"); // Start with a newline for separation
            noteId = MainActivity.notes.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        } */
        if (noteId != -1) {
            String[] noteData = MainActivity.notes.get(noteId).split("\n", 3);
            if (noteData.length == 3) {
                lectureTitleEditText.setText(noteData[0]);
                editText.setText(noteData[2]);
            }
        } else {
            MainActivity.notes.add("");
            noteId = MainActivity.notes.size() - 1;
            MainActivity.arrayAdapter.notifyDataSetChanged();
        }
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // add your code here
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String currentDateAndTime = getCurrentDateAndTime();
                String updatedNote = String.valueOf(charSequence) + "\n\nLast edited: " + currentDateAndTime;
                MainActivity.notes.set(noteId, updatedNote);
                MainActivity.arrayAdapter.notifyDataSetChanged();

                // Creating Object of SharedPreferences to store data in the phone
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
                HashSet<String> set = new HashSet(MainActivity.notes);
                sharedPreferences.edit().putStringSet("notes", set).apply();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // add your code here
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }

    private String getCurrentDateAndTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private void saveNote() {
        // Implement your logic to save the note
        // Combine lecture title and note content
       /*  String lectureTitle = lectureTitleEditText.getText().toString();
        String editedText = lectureTitle + "\n" + editText.getText().toString();

//        String editedText = editText.getText().toString();
//        String currentDateAndTime = getCurrentDateAndTime();
//        String updatedNote = editedText + "\n\nLast edited: " + currentDateAndTime;
//        MainActivity.notes.set(noteId, updatedNote);
//        MainActivity.arrayAdapter.notifyDataSetChanged();

        MainActivity.notes.set(noteId, editedText);
        MainActivity.arrayAdapter.notifyDataSetChanged(); */

        String editedTitle = lectureTitleEditText.getText().toString();
        String editedText = editText.getText().toString();
        String currentDate = DateFormat.getDateTimeInstance().format(new Date()); // Get current date and time
        String combinedNote = editedTitle  + "\n" + editedText + " \n\nLast Edited: " + currentDate;

        MainActivity.notes.set(noteId, combinedNote);
        MainActivity.arrayAdapter.notifyDataSetChanged();

        // Store the updated notes in SharedPreferences
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);
        HashSet<String> set = new HashSet(MainActivity.notes);
        sharedPreferences.edit().putStringSet("notes", set).apply();

        finish(); // Close the NoteEditorActivity after saving
    }
}
