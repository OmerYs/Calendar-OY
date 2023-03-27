package com.example.calendar_oy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
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
    private Spinner categorySpinner;
    private TextView noCategoriesMessage;

    private DatabaseReference bucketListRef;
    private FirebaseAuth mAuth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list);

        noCategoriesMessage = findViewById(R.id.no_categories_message);

        initializeFirebase();
        initializeBucketList();
        setupAddButton();
        setupManageCategoriesButton();
        categorySpinner = findViewById(R.id.category_spinner);
        loadCategories();
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
        adapter = new BucketListItemAdapter(this, R.layout.list_item_with_checkbox, bucketList, bucketListKeys, bucketListRef, userId);
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
                        Category category = item.getCategory();
                        bucketList.add(new BucketListItem(item.getItemName(), item.isChecked(), category));
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
                    Category selectedCategory = (Category) categorySpinner.getSelectedItem();
                    newItemRef.setValue(new BucketListItem(itemName, false, selectedCategory)).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void setupManageCategoriesButton() {
        Button manageCategoriesButton = findViewById(R.id.manage_categories_button);
        manageCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent categoryManagementIntent = new Intent(BucketList.this, CategoryManagement.class);
                startActivity(categoryManagementIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(i);
        finish();
    }

    private void loadCategories() {
        DatabaseReference categoriesRef = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("categories");

        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Category> categories = new ArrayList<>();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categories.add(category);
                    }
                }

                if (categories.isEmpty()) {
                    addButton.setEnabled(false);
                    noCategoriesMessage.setVisibility(View.VISIBLE);
                } else {
                    addButton.setEnabled(true);
                    noCategoriesMessage.setVisibility(View.GONE);
                }

                ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(BucketList.this, android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                categorySpinner.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}

