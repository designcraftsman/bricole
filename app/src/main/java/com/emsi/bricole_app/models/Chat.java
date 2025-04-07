package com.emsi.bricole_app.models;

public class Chat {
    public String name;
    public String lastMessage;
    public String date;
    public int avatar;

    public Chat(String name, String lastMessage, String date, int avatar) {
        this.name = name;
        this.lastMessage = lastMessage;
        this.date = date;
        this.avatar = avatar;
    }
    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getDate() {
        return date;
    }

    public int getAvatar() {
        return avatar;
    }
}
