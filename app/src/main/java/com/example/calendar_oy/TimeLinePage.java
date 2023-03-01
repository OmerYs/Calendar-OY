package com.example.calendar_oy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimeLinePage extends AppCompatActivity {

    private List<HashMap<String, String>> timelineList;
    private TimelineListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line_page);

        timelineList = new ArrayList<>();
        adapter = new TimelineListAdapter(this, timelineList);

        ListView timelineListView = findViewById(R.id.timeline_list_view);
        timelineListView.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("title");
            String date = extras.getString("date");
            String action = extras.getString("action");

            HashMap<String, String> map = new HashMap<>();
            map.put("title", title);
            map.put("date", date);
            map.put("action", action);
            timelineList.add(map);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }
}
