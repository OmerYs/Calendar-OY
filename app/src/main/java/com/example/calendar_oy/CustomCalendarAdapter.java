package com.example.calendar_oy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CustomCalendarAdapter extends BaseAdapter {
    List<Date> dates;
    Calendar calendar;
    List<Events> events;
    LayoutInflater inflater;
    Context context;

    public CustomCalendarAdapter(Context context, List<Date> dates, Calendar calendar, List<Events> events) {
        this.context = context;
        this.dates = dates;
        this.calendar = calendar;
        this.events = events;
        inflater = LayoutInflater.from(context);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        Date monthDate = dates.get(position);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(monthDate);
        int DayNo = dateCalendar.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCalendar.get(Calendar.MONTH) + 1;
        int displayYear = dateCalendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.single_cell_layout, parent, false);
        }
        TextView Day_Number = view.findViewById(R.id.calendar_day);
        Day_Number.setText(String.valueOf(DayNo));
        TextView events_id = view.findViewById(R.id.events_id);
        Calendar eventCalendar = Calendar.getInstance();
        for (int i = 0; i < events.size(); i++) {
            eventCalendar.setTime(events.get(i).getDate());
            if (DayNo == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == currentMonth && displayYear == currentYear) {
                events_id.setBackgroundColor(Color.parseColor("#FF4081"));
            }
        }
        if (displayMonth == currentMonth && displayYear == currentYear) {
            Day_Number.setTextColor(Color.parseColor("#000000"));
        } else {
            Day_Number.setTextColor(Color.parseColor("#666666"));
        }
        if (DayNo == calendar.get(Calendar.DAY_OF_MONTH) && displayMonth == currentMonth && displayYear == currentYear) {
            view.setBackgroundColor(Color.parseColor("#FF5733"));
        } else {
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        return view;
    }
}
