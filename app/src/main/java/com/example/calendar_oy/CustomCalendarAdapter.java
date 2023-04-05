package com.example.calendar_oy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomCalendarAdapter extends BaseAdapter {
    private List<Date> dates;
    private Calendar calendar;
    private List<Events> events;
    private LayoutInflater inflater;

    public CustomCalendarAdapter(Context context, List<Date> dates, Calendar calendar, List<Events> events) {
        this.dates = dates;
        this.calendar = calendar;
        this.events = events;
        inflater = LayoutInflater.from(context);
        fetchEvents();
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);

        int dayValue = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH);
        int displayYear = dateCalendar.get(Calendar.YEAR);

        if (view == null) {
            view = inflater.inflate(R.layout.single_cell_layout, viewGroup, false);
        }

        TextView cellNumber = view.findViewById(R.id.calendar_day);
        cellNumber.setText(String.valueOf(dayValue));

        boolean isEventOnDate = false;
        for (Events event : events) {
            Calendar eventCalendar = Calendar.getInstance();
            eventCalendar.setTime(event.getDate());
            if (dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) &&
                    displayMonth == eventCalendar.get(Calendar.MONTH) &&
                    displayYear == eventCalendar.get(Calendar.YEAR)) {
                isEventOnDate = true;
                break;
            }
        }

        Calendar currentDate = Calendar.getInstance();
        if (displayMonth == currentDate.get(Calendar.MONTH) && displayYear == currentDate.get(Calendar.YEAR) && dayValue == currentDate.get(Calendar.DAY_OF_MONTH)) {
            cellNumber.setTextColor(Color.parseColor("#FF4081"));
        } else if (isEventOnDate) {
            cellNumber.setTextColor(Color.parseColor("#FF5722"));
        } else {
            cellNumber.setTextColor(Color.BLACK);
        }

        return view;
    }


    private void fetchEvents() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("events");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                    Events event = eventSnapshot.getValue(Events.class);
                    if (event != null) {
                        events.add(event);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
