package com.example.calendar_oy;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BucketListItemAdapter extends ArrayAdapter<BucketListItem> {

    private int resource;
    private DatabaseReference bucketListRef;
    private List<String> bucketListKeys;
    private String userId;

    public BucketListItemAdapter(Context context, int resource, List<BucketListItem> objects, List<String> bucketListKeys, DatabaseReference bucketListRef, String userId) {
        super(context, resource, objects);
        this.resource = resource;
        this.bucketListRef = bucketListRef;
        this.bucketListKeys = bucketListKeys;
        this.userId = userId;
    }

    private static class ViewHolder {
        TextView itemName;
        CheckBox checkBox;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            holder = new ViewHolder();
            holder.itemName = convertView.findViewById(R.id.item_text);
            holder.checkBox = convertView.findViewById(R.id.item_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final BucketListItem currentItem = getItem(position);
        if (currentItem != null) {
            holder.itemName.setText(currentItem.getItemName());
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(currentItem.isChecked());
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    currentItem.setChecked(isChecked);

                    String itemKey = bucketListKeys.get(position);
                    bucketListRef.child(itemKey).child("checked").setValue(isChecked);
                }
            });

            holder.itemName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showEditItemDialog(currentItem, position);
                }
            });
        }

        return convertView;
    }

    private void showEditItemDialog(final BucketListItem currentItem, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_edit_item, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();

        final EditText itemNameEditText = view.findViewById(R.id.edit_item_name);
        final Spinner editCategorySpinner = view.findViewById(R.id.edit_category_spinner);
        Button saveButton = view.findViewById(R.id.save_button);
        Button cancelButton = view.findViewById(R.id.cancel_button);
        Button deleteButton = view.findViewById(R.id.delete_button);

        itemNameEditText.setText(currentItem.getItemName());
        loadCategories(editCategorySpinner);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItemName = itemNameEditText.getText().toString();
                Category newCategory = (Category) editCategorySpinner.getSelectedItem();

                if (!newItemName.isEmpty()) {
                    currentItem.setItemName(newItemName);
                    currentItem.setCategory(newCategory);
                    updateItemInFirebase(currentItem, position);
                    notifyDataSetChanged();
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

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemFromFirebase(position);
                remove(currentItem);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadCategories(final Spinner spinner) {
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

                ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(categoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void updateItemInFirebase(BucketListItem item, int position) {
        String itemKey = bucketListKeys.get(position);
        DatabaseReference itemRef = bucketListRef.child(itemKey);
        itemRef.setValue(item);
    }

    private void deleteItemFromFirebase(int position) {
        String itemKey = bucketListKeys.get(position);
        bucketListRef.child(itemKey).removeValue();
    }

}

