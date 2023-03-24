package com.example.calendar_oy;

public class BucketListItem {
    private String itemName;
    private boolean checked;

    public BucketListItem() {
    }

    public BucketListItem(String itemName, boolean checked) {
        this.itemName = itemName;
        this.checked = checked;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
