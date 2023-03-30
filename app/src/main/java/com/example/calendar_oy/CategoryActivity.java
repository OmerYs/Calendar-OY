package com.example.calendar_oy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ArrayList<Item> items;
    private Button addItemButton;
    private DatabaseReference mDatabase;
    private String categoryId;
    private DueItemsNotificationReceiver dueItemsNotificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        categoryId = getIntent().getStringExtra("category_id");
        mDatabase = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("categories").child(categoryId).child("items");

        recyclerView = findViewById(R.id.item_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        items = new ArrayList<>();
        adapter = new ItemAdapter(items, categoryId);
        recyclerView.setAdapter(adapter);

        addItemButton = findViewById(R.id.add_item_button);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CategoryActivity.this);
                builder.setTitle("Add Item");

                final EditText input = new EditText(CategoryActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String itemDescription = input.getText().toString();
                        if (!itemDescription.isEmpty()) {
                            Calendar calendar = Calendar.getInstance();
                            DatePickerDialog datePickerDialog = new DatePickerDialog(CategoryActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    Calendar selectedDate = Calendar.getInstance();
                                    selectedDate.set(year, month, dayOfMonth);
                                    long dueDate = System.currentTimeMillis()+5000/*selectedDate.getTimeInMillis()*/;

                                    String itemId = mDatabase.push().getKey();
                                    Item item = new Item(itemId, itemDescription, false, dueDate);
                                    items.add(item);
                                    mDatabase.child(itemId).setValue(item);
                                    adapter.notifyDataSetChanged();

                                    setAlarm(CategoryActivity.this, itemId, itemDescription, dueDate);
                                }
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                            datePickerDialog.show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        dueItemsNotificationReceiver = new DueItemsNotificationReceiver();
        IntentFilter intentFilter = new IntentFilter("com.example.calendar_oy.ACTION_DUE_ITEM_NOTIFICATION");
        intentFilter.addCategory("android.intent.category.DEFAULT");
        registerReceiver(dueItemsNotificationReceiver, intentFilter);
    }

    private void fetchItems() {
        items.clear();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    Item item = itemSnapshot.getValue(Item.class);
                    if (item != null) {
                        items.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dueItemsNotificationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchItems();
    }

    private void setAlarm(Context context, String itemId, String itemDescription, long dueDate) {
        Intent intent = new Intent(context, DueItemsNotificationReceiver.class);
        intent.setAction("com.example.calendar_oy.ACTION_DUE_ITEM_NOTIFICATION");
        intent.putExtra("itemId", itemId);
        intent.putExtra("itemDescription", itemDescription);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, itemId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, dueDate, pendingIntent);
    }


}

