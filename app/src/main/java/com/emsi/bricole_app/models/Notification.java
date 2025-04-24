package com.emsi.bricole_app.models;

public class Notification {
    private int id;
    private int senderId;
    private String message;
    private String createdAt;

    public Notification(int id, int senderId, String message, String createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.message = message;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }
}
