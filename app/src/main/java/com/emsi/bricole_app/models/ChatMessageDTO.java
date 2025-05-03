package com.emsi.bricole_app.models;

public class ChatMessageDTO {
    private int senderId;
    private int receiverId;
    private String room;
    private String content;
    private String senderFirstName;
    private String senderLastName;

    public ChatMessageDTO() {}

    // Getters and Setters
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSenderFirstName() { return senderFirstName; }
    public void setSenderFirstName(String senderFirstName) { this.senderFirstName = senderFirstName; }

    public String getSenderLastName() { return senderLastName; }
    public void setSenderLastName(String senderLastName) { this.senderLastName = senderLastName; }
}
