package com.example.calendar_oy;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        boolean isEventOnDate = isEventOnDate(dayValue, displayMonth, displayYear, events);

        Calendar currentDate = Calendar.getInstance();
        if (displayMonth == currentDate.get(Calendar.MONTH) && displayYear == currentDate.get(Calendar.YEAR) && dayValue == currentDate.get(Calendar.DAY_OF_MONTH)) {
            cellNumber.setTextColor(Color.parseColor("#FF4081"));
            view.setBackgroundColor(Color.parseColor("#E0F2F1"));
        } else if (isEventOnDate) {
            cellNumber.setTextColor(Color.parseColor("#FF5722"));
            view.setBackgroundColor(Color.parseColor("#FFF3E0"));
        } else {
            cellNumber.setTextColor(Color.BLACK);
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        return view;
    }

    private boolean isEventOnDate(int dayValue, int displayMonth, int displayYear, List<Events> events) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.set(Calendar.YEAR, displayYear);
        dateCalendar.set(Calendar.MONTH, displayMonth);
        dateCalendar.set(Calendar.DAY_OF_MONTH, dayValue);

        for (Events event : events) {
            if (event.getDate() == null) {
                continue;
            }

            Calendar eventCalendar = Calendar.getInstance();
            try {
                Date eventDate = dateFormat.parse(event.getDate());
                eventCalendar.setTime(eventDate);
            } catch (ParseException e) {
                e.printStackTrace();
                continue;
            }

            if (eventCalendar.get(Calendar.DAY_OF_MONTH) == dayValue &&
                    eventCalendar.get(Calendar.MONTH) == displayMonth &&
                    eventCalendar.get(Calendar.YEAR) == displayYear) {
                String eventDateString = dateFormat.format(eventCalendar.getTime());
                String monthDateString = dateFormat.format(dateCalendar.getTime());
                if (eventDateString.equals(monthDateString)) {
                    return true;
                }
            }
        }
        return false;
    }
}