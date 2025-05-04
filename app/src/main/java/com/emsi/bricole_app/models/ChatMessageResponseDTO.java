package com.emsi.bricole_app.models;

public class ChatMessageResponseDTO {
    private int senderId;          // Flat senderId for deserialization fallback
    private Sender sender;         // Nested sender object
    private String content;
    private String time;

    public ChatMessageResponseDTO() {}

    public int getSenderId() {
        // Use sender object if available, fallback to senderId field
        return sender != null ? sender.getId() : senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    // Nested class for sender
    public static class Sender {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
