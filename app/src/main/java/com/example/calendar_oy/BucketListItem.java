package com.example.calendar_oy;

public class BucketListItem {
    private String name;
    private String date;
    private String description;

    public BucketListItem() {

    }

    public BucketListItem(String name, String date, String description) {
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}

