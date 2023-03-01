package com.example.calendar_oy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;

public class NotesPage extends AppCompatActivity {

    private ArrayList<String> notesList;
    private ArrayAdapter<String> adapter;
    private EditText noteEditText;
    private int selectedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_page);

        notesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesList);

        ListView notesListView = findViewById(R.id.notes_list_view);
        notesListView.setAdapter(adapter);
        notesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                noteEditText.setText(notesList.get(position));
                selectedPosition = position;
            }
        });

        noteEditText = findViewById(R.id.note_edit_text);
    }

    public void addNote(View view) {
        String noteText = noteEditText.getText().toString();
        if (!noteText.isEmpty()) {
            if (selectedPosition != -1) {
                notesList.set(selectedPosition, noteText);
                selectedPosition = -1;
            } else {
                notesList.add(noteText);
            }
            adapter.notifyDataSetChanged();
            noteEditText.getText().clear();
        }
    }

    public void deleteNote(View view) {
        if (selectedPosition != -1) {
            notesList.remove(selectedPosition);
            adapter.notifyDataSetChanged();
            noteEditText.getText().clear();
            selectedPosition = -1;
        }
    }
}
