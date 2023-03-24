package com.example.calendar_oy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class BucketListItemAdapter extends ArrayAdapter<BucketListItem> {

    private int resource;

    public BucketListItemAdapter(Context context, int resource, List<BucketListItem> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }

        TextView itemText = convertView.findViewById(R.id.item_text);
        CheckBox itemCheckbox = convertView.findViewById(R.id.item_checkbox);

        BucketListItem currentItem = getItem(position);
        if (currentItem != null) {
            itemText.setText(currentItem.getItemName());
            itemCheckbox.setChecked(currentItem.isChecked());
        }

        return convertView;
    }
}
