package com.emsi.bricole_app.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Chat_Conversation extends Drawer {
    private ImageButton mBackBtn;
    private int receiver_id;

    private SharedPreferences prefs;

    private String USER_ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_chat_conversation);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        mBackBtn = findViewById(R.id.backButton);
        mBackBtn.setOnClickListener(view -> finish());

        receiver_id = getIntent().getIntExtra("receiver_id", -1);

        if (receiver_id != -1) {
            startConversation(receiver_id);
        }
    }

    private void startConversation(int receiver_id) {
        String jwtToken = "Bearer " + USER_ACCESS_TOKEN; //

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("id",0);
            jsonBody.put("user1Id",0);
            jsonBody.put("user2Id", receiver_id);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "http://10.0.2.2:8080/api/chat/conversation/new", // ⬅️ update with actual IP/port
                jsonBody,
                response -> {
                    int conversationId = response.optInt("id");
                    // ✅ Now use this ID to fetch messages or open WebSocket
                },
                error -> {
                    // ❌ Handle error
                    Log.e("Chat", "Conversation error: " + error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", jwtToken);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }



}
