package com.emsi.bricole_app.controllers;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import com.emsi.bricole_app.controllers.Activity_ChatAdapter;

public class Activity_Chat_Conversation extends  BaseDrawerActivity{

    private ImageButton mBackBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This method is defined in BaseDrawerActivity and inflates the DrawerLayout
        setupDrawer(R.layout.activity_chat_conversation);

        mBackBtn = findViewById(R.id.backButton);

        mBackBtn.setOnClickListener(view->{
            finish();
        });
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage("Are you still travelling?", "3:00 PM", ChatMessage.TYPE_RECEIVED));
        chatMessages.add(new ChatMessage("Yes, I’m at Istanbul..", "3:00 PM", ChatMessage.TYPE_SENT));
        chatMessages.add(new ChatMessage("OoOo, That's so cool!", "3:01 PM", ChatMessage.TYPE_RECEIVED));
        chatMessages.add(new ChatMessage("Raining??", "3:01 PM", ChatMessage.TYPE_RECEIVED));

        Activity_ChatAdapter chatAdapter = new Activity_ChatAdapter(chatMessages);
        RecyclerView recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

}
