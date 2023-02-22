package com.example.calendar_oy;

import java.util.Date;

public class Events {
    private String eventName;
    private Date date;

    public Events(String eventName, Date date) {
        this.eventName = eventName;
        this.date = date;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}

