package com.example.calendar_oy;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        mDatabase = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("categories").child(categoryId).child("items");
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
                DatabaseReference itemReference = mDatabase.child(currentItem.getId());
                itemReference.child("complete").setValue(isChecked);

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Item currentItem = items.get(position);
                        showEditDialog(v.getContext(), currentItem);
                    }
                }
            });
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

    private void showEditDialog(Context context, Item item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_edit_dialog, null);

        EditText itemNameEditText = view.findViewById(R.id.item_edit_name);
        Button changeDueDateButton = view.findViewById(R.id.item_edit_due_date);
        Button deleteButton = view.findViewById(R.id.item_edit_delete);
        Button saveButton = view.findViewById(R.id.item_edit_save);

        itemNameEditText.setText(item.getDescription());

        changeDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(item.getDueDate());
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDate.set(Calendar.MINUTE, minute);
                                item.setDueDate(selectedDate.getTimeInMillis());
                            }
                        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                        timePickerDialog.show();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child(item.getId()).removeValue();
                items.remove(item);
                notifyDataSetChanged();
                ((CategoryActivity) context).cancelAlarm(context, item.getId());
                ((AlertDialog) v.getTag()).dismiss();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItemName = itemNameEditText.getText().toString();
                if (!newItemName.isEmpty()) {
                    item.setDescription(newItemName);
                    mDatabase.child(item.getId()).child("description").setValue(newItemName);
                    mDatabase.child(item.getId()).child("dueDate").setValue(item.getDueDate());
                    notifyDataSetChanged();
                    ((CategoryActivity) context).cancelAlarm(context, item.getId());
                    ((CategoryActivity) context).setAlarm(context, item.getId(), newItemName, item.getDueDate());
                }
                ((AlertDialog) v.getTag()).dismiss();
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        changeDueDateButton.setTag(dialog);
        deleteButton.setTag(dialog);
        saveButton.setTag(dialog);
        dialog.show();
    }
}
