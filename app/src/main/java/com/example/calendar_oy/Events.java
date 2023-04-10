package com.example.calendar_oy;

public class Events {
    private String title;
    private String date;
    private String eventKey;

    public Events() {
    }

    public Events(String title, String date) {
        this.title = title;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEventKey() { return eventKey; }

    public void setEventKey(String eventKey) { this.eventKey = eventKey; }

    @Override
    public String toString() {
        return "Title: " + title + ", Date: " + date;
    }
}

