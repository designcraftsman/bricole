package com.emsi.bricole_app.models;

public class Job {
    private int id;
    private String postedBy;
    private String title;
    private String location;
    private String timePosted;
    private boolean isNew;

    public Job(int id,String postedBy, String title, String location, String timePosted, boolean isNew) {
        this.id = id;
        this.postedBy = postedBy;
        this.title = title;
        this.location = location;
        this.timePosted = timePosted;
        this.isNew = isNew;
    }

    public String getPostedBy() { return postedBy; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getTimePosted() { return timePosted; }
    public boolean isNew() { return isNew; }

    public int getId() {
        return id;
    }

}
