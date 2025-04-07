package com.emsi.bricole_app.models;

public class ChatMessage {
    public static final int TYPE_SENT = 0;
    public static final int TYPE_RECEIVED = 1;

    public String message;
    public String time;
    public int type;

    public ChatMessage(String message, String time, int type) {
        this.message = message;
        this.time = time;
        this.type = type;
    }
}

