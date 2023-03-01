package com.example.calendar_oy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;

public class TimelineListAdapter extends BaseAdapter {

    private Context context;
    private List<HashMap<String, String>> list;

    public TimelineListAdapter(Context context, List<HashMap<String, String>> list) {
        this.context = context;
        this.list = list;
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
        TextView actionTextView = convertView.findViewById(R.id.action_text_view);

        HashMap<String, String> item = list.get(position);
        String title = item.get("title");
        String date = item.get("date");
        String action = item.get("action");

        titleTextView.setText(title);
        dateTextView.setText(date);
        actionTextView.setText(action);

        return convertView;
    }
}
