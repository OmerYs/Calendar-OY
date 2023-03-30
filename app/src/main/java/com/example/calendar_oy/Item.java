package com.example.calendar_oy;

public class Item {
    private String id;
    private String description;
    private boolean isComplete;
    private Long dueDate;

    public Item(){
    }

    public Item(String id, String description, boolean isComplete, long dueDate) {
        this.id = id;
        this.description = description;
        this.isComplete = isComplete;
        this.dueDate = dueDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public void setDescription(String description) { this.description = description; }

    public String getDescription() { return description; }

    public boolean isComplete() { return isComplete; }

    public void setComplete(boolean complete) { isComplete = complete; }

    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
    }
}

