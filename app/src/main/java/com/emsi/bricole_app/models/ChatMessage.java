package com.emsi.bricole_app.models;

public class ChatMessage {
    public static final int TYPE_SENT = 0;
    public static final int TYPE_RECEIVED = 1;

    private int senderId;
    private int receiverId;
    private int conversationId;
    private String content;
    private String time;
    private int type;

    public ChatMessage() {}

    public ChatMessage(String content, String time, int type) {
        this.content = content;
        this.time = time;
        this.type = type;
    }

    // Getters and setters...
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }

    public int getConversationId() { return conversationId; }
    public void setConversationId(int conversationId) { this.conversationId = conversationId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }
}
