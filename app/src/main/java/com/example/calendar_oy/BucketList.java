package com.example.calendar_oy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBar;

public class BucketList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private ArrayList<Category> categories;
    private Button addCategoryButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String userId;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        sharedPref.applyTheme(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucket_list);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        userId = user.getUid();
        mDatabase = FirebaseDatabase.getInstance("https://calendarproject-dae43-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(userId).child("categories");

        recyclerView = findViewById(R.id.category_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categories = new ArrayList<>();
        adapter = new CategoryAdapter(categories, this);
        recyclerView.setAdapter(adapter);

        addCategoryButton = findViewById(R.id.add_category_button);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BucketList.this);
                builder.setTitle("Add Category");

                final EditText input = new EditText(BucketList.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String categoryName = input.getText().toString();
                        if (!categoryName.isEmpty()) {
                            String categoryId = mDatabase.push().getKey();
                            Category category = new Category(categoryId, categoryName);
                            categories.add(category);
                            mDatabase.child(categoryId).setValue(category);
                            adapter.notifyDataSetChanged();
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

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categories.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    String categoryId = categorySnapshot.getKey();
                    String categoryName = categorySnapshot.child("name").getValue(String.class);
                    if (categoryName != null) {
                        Category category = new Category(categoryId, categoryName);
                        categories.add(category);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
