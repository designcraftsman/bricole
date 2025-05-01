package com.emsi.bricole_app.models;
public class Chat {
    public int chatId;
    public int otherUserId;
    public String name;
    public String lastMessage;
    public String date;
    public String profilePictureUrl;

    public Chat(int chatId, int otherUserId, String name, String lastMessage, String date, String profilePictureUrl) {
        this.chatId = chatId;
        this.otherUserId = otherUserId;
        this.name = name;
        this.lastMessage = lastMessage;
        this.date = date;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getName() { return name; }
    public String getLastMessage() { return lastMessage; }
    public String getDate() { return date; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
}
