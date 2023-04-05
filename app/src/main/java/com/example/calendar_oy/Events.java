package com.example.calendar_oy;

import java.util.Date;

public class Events {
    private String title;
    private long date;
    private String eventKey;

    public Events() {
    }

    public Events(String title, Date date) {
        this.title = title;
        this.date = date.getTime();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return new Date(date);
    }

    public void setDate(Date date) {
        this.date = date.getTime();
    }

    public String getEventKey() { return eventKey; }

    public void setEventKey(String eventKey) { this.eventKey = eventKey; }

    @Override
    public String toString() {
        return title;
    }
}

