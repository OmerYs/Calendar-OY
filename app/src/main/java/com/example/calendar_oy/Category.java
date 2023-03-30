package com.example.calendar_oy;

import java.util.ArrayList;

public class Category {
    private String id;
    private String name;
    private ArrayList<Item> items;

    public Category(){
    }

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
        items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() { return name; }

    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public ArrayList<Item> getItems() {
        return items;
    }
}
