package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Chat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_Messages_Listing extends Drawer {

    RecyclerView chatRecyclerView;
    Activity_MessagesAdapter adapter;
    ArrayList<Chat> chatMessages;

    private ImageButton mBackBtn;

    private SharedPreferences prefs;

    private String USER_ACCESS_TOKEN;
    private int  user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_messages_listing);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);
        user_id = prefs.getInt("user_id", -1);

        mBackBtn = findViewById(R.id.backButton);

        mBackBtn.setOnClickListener(view->{
            finish();
        });

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatMessages = new ArrayList<>();
        adapter = new Activity_MessagesAdapter(this, chatMessages, chat -> {
            Intent intent = new Intent(Activity_Messages_Listing.this, Activity_Chat_Conversation.class);
            intent.putExtra("name", chat.getName());
            intent.putExtra("chat_id", chat.chatId);
            intent.putExtra("other_user_id", chat.otherUserId);
            startActivity(intent);
        });

        chatRecyclerView.setAdapter(adapter);

        fetchConversations(); // 🔥 THIS IS WHAT WAS MISSING


    }

    private void fetchConversations() {
        String jwt = "Bearer " + USER_ACCESS_TOKEN; // From SharedPreferences

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                "http://10.0.2.2:8080/api/chat/conversations", // Update URL
                null,
                response -> {
                    chatMessages.clear();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject convo = response.getJSONObject(i);
                            JSONObject user1 = convo.getJSONObject("user1");
                            JSONObject user2 = convo.getJSONObject("user2");

                            JSONObject otherUser = (user1.getInt("id") == user_id) ? user2 : user1;
                            String name = otherUser.getString("firstname") + " " + otherUser.getString("lastname");
                            String profilePic = otherUser.getString("profilePicture");
                            String lastMsg = convo.optString("lastMessage", "");
                            String lastMsgAt = convo.optString("lastMessageAt", "");
                            String profilePicture = otherUser.getString("profilePicture");

                            chatMessages.add(new Chat(
                                    convo.getInt("id"),
                                    otherUser.getInt("id"),
                                    name,
                                    lastMsg,
                                    lastMsgAt != null ? lastMsgAt : "",
                                    profilePicture
                            ));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Log.e("ChatFetch", "Error: " + error.toString())
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}
