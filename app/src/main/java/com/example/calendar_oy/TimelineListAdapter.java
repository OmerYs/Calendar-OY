package com.example.calendar_oy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimelineListAdapter extends BaseAdapter {

    private Context context;
    private List<Item> list;

    public TimelineListAdapter(Context context, List<Item> list) {
        this.context = context;
        this.list = list;
        sortByDueDate();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.timeline_list_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.title_text_view);
        TextView dateTextView = convertView.findViewById(R.id.date_text_view);

        Item item = list.get(position);
        String title = item.getDescription();
        long dateMillis = item.getDueDate();
        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        String date = dateFormat.format(new Date(dateMillis));

        titleTextView.setText(title);
        dateTextView.setText(date);

        return convertView;
    }

    private void sortByDueDate() {
        Collections.sort(list, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return item1.getDueDate().compareTo(item2.getDueDate());
            }
        });
    }
}
