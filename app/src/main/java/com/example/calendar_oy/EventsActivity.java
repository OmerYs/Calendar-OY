package com.example.calendar_oy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EventsActivity extends AppCompatActivity {

    TextView dateTextView;
    EditText eventEditText;
    Button addButton;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
    String date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dateTextView = findViewById(R.id.dateTextView);
        eventEditText = findViewById(R.id.eventEditText);
        addButton = findViewById(R.id.addButton);

        // Get the date from the intent
        date = getIntent().getStringExtra("date");

        // Set the date on the text view
        dateTextView.setText(dateFormat.format(calendar.getTime()));

        // Initialize Firebase database
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("Events");

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the event name from the edit text
                String eventName = eventEditText.getText().toString();

                // Check if the event name is empty
                if (eventName.isEmpty()) {
                    Toast.makeText(EventsActivity.this, "Please enter an event name", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new event object
                Events event = new Events(date, eventName);

                // Add the event to the database
                reference.child(date).push().setValue(event);

                // Show a success message
                Toast.makeText(EventsActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();

                // Clear the edit text
                eventEditText.setText("");
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Show a confirmation dialog when the back button is pressed
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to go back? Any unsaved changes will be lost.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Finish the activity if the user confirms
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
