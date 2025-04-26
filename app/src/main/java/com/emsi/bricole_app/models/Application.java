package com.emsi.bricole_app.models;

public class Application {
    private int id;
    private String title;
    private String location;
    private String createdAt;
    private String state;

    public Application(int id,String title, String location, String createdAt, String state) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.createdAt = createdAt;
        this.state = state;
    }


    public int getId() { return id;}
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getCreatedAt() { return createdAt; }
    public String getState() { return state; }
}
