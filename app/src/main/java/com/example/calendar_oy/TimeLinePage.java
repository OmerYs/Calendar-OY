package com.example.calendar_oy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimeLinePage extends AppCompatActivity {

    private List<Item> timelineList;
    private TimelineListAdapter adapter;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String userId;

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        sharedPref.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_page);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            // Handle the case when the user is not logged in
        }

        timelineList = new ArrayList<>();
        adapter = new TimelineListAdapter(this, timelineList);

        ListView timelineListView = findViewById(R.id.timeline_list_view);
        timelineListView.setAdapter(adapter);

        mDatabase = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("categories");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timelineList.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot itemSnapshot : categorySnapshot.child("items").getChildren()) {
                        Item item = itemSnapshot.getValue(Item.class);
                        if (item != null) {
                            item.setId(itemSnapshot.getKey());
                            timelineList.add(item);
                            Log.d("TimeLinePage", "Item added: " + item.getDescription() + ", Due Date: " + item.getDueDate());
                        }
                    }
                }
                Collections.sort(timelineList, new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        if (o1.getDueDate() != null && o2.getDueDate() != null) {
                            return o1.getDueDate().compareTo(o2.getDueDate());
                        }
                        return 0;
                    }
                });
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}
