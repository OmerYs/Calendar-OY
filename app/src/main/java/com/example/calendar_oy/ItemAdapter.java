package com.example.calendar_oy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private ArrayList<Item> items;
    private DatabaseReference mDatabase;
    private String userId;
    private String categoryId;

    public ItemAdapter(ArrayList<Item> items, String categoryId) {
        this.items = items;
        this.categoryId = categoryId;
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item currentItem = items.get(position);
        holder.itemDescription.setText(currentItem.getDescription());
        holder.itemStatus.setChecked(currentItem.isComplete());
        holder.itemDueDate.setText(getFormattedDate(currentItem.getDueDate()));

        holder.itemStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentItem.setComplete(isChecked);
                mDatabase = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("categories").child(categoryId).child("items").child(currentItem.getId());
                mDatabase.child("complete").setValue(isChecked);

                Context context = buttonView.getContext();
                cancelAlarm(context, currentItem.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView itemDescription;
        public CheckBox itemStatus;
        public TextView itemDueDate;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemDescription = itemView.findViewById(R.id.item_description);
            itemStatus = itemView.findViewById(R.id.item_checkbox);
            itemDueDate = itemView.findViewById(R.id.item_due_date);
        }
    }

    private String getFormattedDate(long dueDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date(dueDate));
    }

    private void cancelAlarm(Context context, String itemId) {
        Intent intent = new Intent("com.example.calendar_oy.ACTION_DUE_ITEM_NOTIFICATION");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, itemId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
