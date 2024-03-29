package com.example.calendar_oy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    private TextView dateTitle;
    private EditText eventName;
    private Button addEventButton;
    private ListView eventsListView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private ArrayList<Events> eventsList = new ArrayList<>();
    private ArrayAdapter<Events> eventsArrayAdapter;

    private DatabaseReference databaseReference;
    private String formattedDate;

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        sharedPref.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        dateTitle = findViewById(R.id.dateTitle);
        eventName = findViewById(R.id.eventName);
        addEventButton = findViewById(R.id.addEventButton);
        eventsListView = findViewById(R.id.eventsListView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        String date = getIntent().getStringExtra("date");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date parsedDate = dateFormat.parse(date);
            SimpleDateFormat eventDateFormat = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.ENGLISH);
            formattedDate = eventDateFormat.format(parsedDate);
            dateTitle.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(currentUser.getUid()).child("events");

        eventsArrayAdapter = new ArrayAdapter<Events>(this, android.R.layout.simple_list_item_1, eventsList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View listItem = convertView;
                if (listItem == null) {
                    listItem = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                }

                Events currentEvent = eventsList.get(position);

                TextView title = listItem.findViewById(android.R.id.text1);
                title.setText(currentEvent.getTitle());

                return listItem;
            }
        };

        eventsListView.setAdapter(eventsArrayAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("Data Change", "Data changed");
                eventsList.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Events event = eventSnapshot.getValue(Events.class);
                    if (event != null && event.getTitle() != null && event.getDate().equals(date)) {
                        event.setEventKey(eventSnapshot.getKey());
                        eventsList.add(event);
                        Log.d("Event Added", "Event: " + event);
                    }
                }
                eventsArrayAdapter.notifyDataSetChanged();
                Log.d("Events List Size", "Size: " + eventsList.size());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Database Error", "Error: ", databaseError.toException());
            }
        });

        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eventTitle = eventName.getText().toString().trim();
                if (!eventTitle.isEmpty()) {
                    Events event = new Events(eventTitle, date);
                    Log.d("Event Created", "Event: " + eventTitle + ", Date: " + date);
                    databaseReference.push().setValue(event);
                    Log.d("Event Stored", "Event: " + event.getTitle() + ", Date: " + event.getDate());
                    eventName.setText("");
                }
            }
        });

        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Events selectedEvent = eventsList.get(position);
                String eventKey = selectedEvent.getEventKey();

                AlertDialog.Builder builder = new AlertDialog.Builder(EventsActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_edit_event, null);
                builder.setView(dialogView);

                EditText editEventName = dialogView.findViewById(R.id.editEventName);
                Button deleteEventButton = dialogView.findViewById(R.id.deleteEventButton);
                Button saveEventButton = dialogView.findViewById(R.id.saveEventButton);

                editEventName.setText(selectedEvent.getTitle());

                AlertDialog alertDialog = builder.create();

                deleteEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseReference.child(eventKey).removeValue();
                        alertDialog.dismiss();
                    }
                });

                saveEventButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String updatedEventName = editEventName.getText().toString().trim();
                        if (!updatedEventName.isEmpty()) {
                            Events updatedEvent = new Events(updatedEventName, selectedEvent.getDate());
                            Log.d("Event Updated", "Event: " + updatedEventName + ", Date: " + selectedEvent.getDate());
                            updatedEvent.setEventKey(eventKey);
                            databaseReference.child(eventKey).setValue(updatedEvent);
                            alertDialog.dismiss();
                        }
                    }
                });

                alertDialog.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), TableCalendar.class);
                startActivity(i);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


