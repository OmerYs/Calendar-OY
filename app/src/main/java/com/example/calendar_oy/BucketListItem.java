package com.example.calendar_oy;

public class BucketListItem {
    private String itemName;
    private boolean checked;
    private Category category;

    public BucketListItem() {
    }

    public BucketListItem(String itemName, boolean isChecked, Category category) {
        this.itemName = itemName;
        this.checked = isChecked;
        this.category = category;
    }

    public String getItemName() { return itemName; }

    public void setItemName(String itemName) { this.itemName = itemName; }

    public boolean isChecked() { return checked; }

    public void setChecked(boolean checked) { this.checked = checked; }

    public Category getCategory() { return category; }

    public void setCategory(Category category) { this.category = category; }
}
