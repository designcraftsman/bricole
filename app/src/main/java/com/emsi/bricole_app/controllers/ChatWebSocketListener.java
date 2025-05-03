package com.emsi.bricole_app.controllers;

import android.util.Log;

import com.emsi.bricole_app.models.ChatMessage;
import com.google.gson.Gson;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatWebSocketListener extends WebSocketListener {
    private final Activity_Chat_Conversation activity;

    public ChatWebSocketListener(Activity_Chat_Conversation activity) {
        this.activity = activity;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        Log.d("WebSocket", "Connection opened");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.d("WebSocket", "Received: " + text);
        activity.runOnUiThread(() -> {
            ChatMessage message = new Gson().fromJson(text, ChatMessage.class);
            message.setType(ChatMessage.TYPE_RECEIVED);
            activity.addMessage(message);
        });
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e("WebSocket", "Error: " + t.getMessage());
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.d("WebSocket", "Closed: " + reason);
    }
}
