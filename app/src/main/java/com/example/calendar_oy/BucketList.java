package com.example.calendar_oy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BucketList extends AppCompatActivity {

    private static final String TAG = "BucketList";

    private ArrayList<BucketListItem> bucketList;
    private ArrayList<String> bucketListKeys;
    private BucketListItemAdapter adapter;
    private EditText itemEditText;
    private Button addButton;

    private DatabaseReference bucketListRef;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list);

        initializeFirebase();
        initializeBucketList();
        setupAddButton();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userId = user.getUid();
            Log.d("BucketList", "User ID: " + userId);
        } else {
            Log.e("BucketList", "User not logged in");
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/");
        bucketListRef = database.getReference("users").child(userId).child("bucketlist");
    }

    private void initializeBucketList() {
        bucketList = new ArrayList<>();
        bucketListKeys = new ArrayList<>();
        adapter = new BucketListItemAdapter(this, R.layout.list_item_with_checkbox, bucketList);
        ListView bucketListView = findViewById(R.id.bucket_list_view);
        bucketListView.setAdapter(adapter);

        bucketListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bucketList.clear();
                bucketListKeys.clear();
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    BucketListItem item = itemSnapshot.getValue(BucketListItem.class);
                    String key = itemSnapshot.getKey();
                    if (item != null) {
                        bucketList.add(new BucketListItem(item.getItemName(), item.isChecked()));
                        bucketListKeys.add(key);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Error reading data", databaseError.toException());
            }
        });
    }

    private void setupAddButton() {
        itemEditText = findViewById(R.id.item_edit_text);
        addButton = findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = itemEditText.getText().toString();
                if (!itemName.isEmpty()) {
                    DatabaseReference newItemRef = bucketListRef.push();
                    newItemRef.setValue(new BucketListItem(itemName, false)).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("BucketList", "Item added successfully: " + itemName);
                            Toast.makeText(BucketList.this, "Item added successfully: " + itemName, Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("BucketList", "Error adding item: " + itemName, e);
                            Toast.makeText(BucketList.this, "Error adding item: " + itemName, Toast.LENGTH_SHORT).show();
                        }
                    });
                    itemEditText.getText().clear();
                }
            }
        });
    }
}

