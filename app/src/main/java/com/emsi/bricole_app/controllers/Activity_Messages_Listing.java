package com.emsi.bricole_app.controllers;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Chat;
import com.emsi.bricole_app.models.Job;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Messages_Listing extends Drawer {

    RecyclerView chatRecyclerView;
    Activity_MessagesAdapter adapter;
    ArrayList<Chat> chatMessages;

    private TextView txtEmptyChat;
    private ImageButton mBackBtn;
    private EditText searchInput;
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

        txtEmptyChat = findViewById(R.id.txtEmptyChat);
        mBackBtn = findViewById(R.id.backButton);
        searchInput = findViewById(R.id.searchInput);

        txtEmptyChat.setVisibility(INVISIBLE);


        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMessagesByName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        mBackBtn.setOnClickListener(view->{
            finish();
        });

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatMessages = new ArrayList<>();
        adapter = new Activity_MessagesAdapter(this, chatMessages, chat -> {
            Intent intent = new Intent(Activity_Messages_Listing.this, Activity_Chat_Conversation.class);
            intent.putExtra("user2_name", chat.getName());
            intent.putExtra("conversation_id", chat.chatId);
            intent.putExtra("user2_image", chat.getProfilePictureUrl());
            intent.putExtra("receiver_id", chat.otherUserId);
            intent.putExtra("isNewConversation", false);
            startActivity(intent);
        });


        chatRecyclerView.setAdapter(adapter);

        fetchConversations(); // 🔥 THIS IS WHAT WAS MISSING


    }

    private void filterMessagesByName(String query) {
        List<Chat> filteredList = new ArrayList<>();
        for (Chat chat : chatMessages) {
            if (chat.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(chat);
            }
        }
        adapter.updateList(filteredList);
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
