package com.agpitcodeclub.codeclubagpit.model;

public class EventModel {
    private String eventId;
    private String title;
    private String description;
    private String date; // Consider using a Date type for better handling
    private String time; // Consider using a Time type for better handling
    private String location;

    public EventModel() {
        // Default constructor required for calls to DataSnapshot.getValue(EventModel.class)
    }

    public EventModel(String eventId, String title, String description, String date, String time, String location) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.location = location;
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
