package com.example.calendar_oy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CategoryManagement extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String userId;
    private DatabaseReference categoryRef;

    private EditText categoryEditText;
    private Button addCategoryButton;
    private ListView categoryListView;
    private ArrayList<Category> categoryList;
    private ArrayAdapter<Category> categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        initializeFirebase();
        initializeViews();
        initializeCategoryList();
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            userId = user.getUid();
        } else {

        }

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/");
        categoryRef = database.getReference("users").child(userId).child("categories");
    }

    private void initializeViews() {
        categoryEditText = findViewById(R.id.category_edit_text);
        addCategoryButton = findViewById(R.id.add_category_button);
        categoryListView = findViewById(R.id.category_list_view);

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String categoryName = categoryEditText.getText().toString();
                if (!categoryName.isEmpty()) {
                    DatabaseReference newCategoryRef = categoryRef.push();
                    String categoryId = newCategoryRef.getKey();
                    newCategoryRef.setValue(new Category(categoryId, categoryName));
                    categoryEditText.getText().clear();
                }
            }
        });
    }

    private void initializeCategoryList() {
        categoryList = new ArrayList<>();
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryList);
        categoryListView.setAdapter(categoryAdapter);

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryList.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    Category category = categorySnapshot.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = categoryList.get(position);
                showEditCategoryDialog(selectedCategory);
            }
        });

    }

    private void showEditCategoryDialog(final Category currentCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CategoryManagement.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_category, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        final EditText editCategoryName = view.findViewById(R.id.edit_category_name);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button deleteCategoryButton = view.findViewById(R.id.delete_category_button);

        editCategoryName.setText(currentCategory.getName());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategoryName = editCategoryName.getText().toString();

                if (!newCategoryName.isEmpty()) {
                    currentCategory.setName(newCategoryName);
                    updateCategoryInFirebase(currentCategory);
                    updateItemCategoriesInFirebase(currentCategory);
                    categoryAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        deleteCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCategoryFromFirebase(currentCategory);
                categoryAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateCategoryInFirebase(Category category) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("categories").child(category.getId());
        categoryRef.setValue(category);
    }

    private void deleteCategoryFromFirebase(Category category) {
        setDefaultCategoryForItems(category);
        DatabaseReference categoryRef = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("categories").child(category.getId());
        categoryRef.removeValue();
    }

    private void updateItemCategoriesInFirebase(Category category) {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("bucketlist");


        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    BucketListItem item = itemSnapshot.getValue(BucketListItem.class);
                    if (item != null && item.getCategory().getId().equals(category.getId())) {
                        item.setCategory(category);
                        DatabaseReference itemRef = itemsRef.child(itemSnapshot.getKey());
                        itemRef.setValue(item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setDefaultCategoryForItems(Category category) {
        DatabaseReference itemsRef = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("bucketlist");
        Category defaultCategory = new Category("default", "Uncategorized");

        itemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    BucketListItem item = itemSnapshot.getValue(BucketListItem.class);
                    if (item != null && item.getCategory().getId().equals(category.getId())) {
                        item.setCategory(defaultCategory);
                        DatabaseReference itemRef = itemsRef.child(itemSnapshot.getKey());
                        itemRef.setValue(item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
