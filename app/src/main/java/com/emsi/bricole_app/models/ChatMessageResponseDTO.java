package com.emsi.bricole_app.models;

public class ChatMessageResponseDTO {
    private int senderId;
    private String content;
    private String time;

    public ChatMessageResponseDTO() {}

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
}
