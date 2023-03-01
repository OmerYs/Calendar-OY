package com.example.calendar_oy;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BucketList extends AppCompatActivity {

    private ArrayList<String> bucketList;
    private ArrayAdapter<String> adapter;
    private EditText itemEditText;

    private DatabaseReference bucketListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        bucketListRef = database.getReference("bucketlist");

        bucketList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bucketList);

        ListView bucketListView = findViewById(R.id.bucket_list_view);
        bucketListView.setAdapter(adapter);
        bucketListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bucketList.remove(position);
                adapter.notifyDataSetChanged();
            }
        });

        itemEditText = findViewById(R.id.item_edit_text);
    }

    public void addItem(View view) {
        String itemName = itemEditText.getText().toString();
        String itemDate = "fix me later";
        String itemDescription = "fix me later";
        if (!itemName.isEmpty()) {
            BucketListItem newItem = new BucketListItem(itemName, itemDate, itemDescription);

            bucketListRef.push().setValue(newItem);

            bucketList.add(itemName);
            adapter.notifyDataSetChanged();
            itemEditText.getText().clear();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}

