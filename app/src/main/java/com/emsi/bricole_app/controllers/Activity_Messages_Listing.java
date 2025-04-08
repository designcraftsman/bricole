package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emsi.bricole_app.R;
import com.emsi.bricole_app.controllers.Activity_MessagesAdapter;
import com.emsi.bricole_app.models.Chat;

import java.util.ArrayList;

public class Activity_Messages_Listing extends BaseDrawerActivity {

    RecyclerView chatRecyclerView;
    Activity_MessagesAdapter adapter;
    ArrayList<Chat> chatMessages;

    private ImageButton mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_messages_listing);


        mBackBtn = findViewById(R.id.backButton);

        mBackBtn.setOnClickListener(view->{
            finish();
        });

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Dummy data
        chatMessages = new ArrayList<>();
        chatMessages.add(new Chat("Smith Mathew", "Hi, David. Hope you’re doing well!", "29 mar", R.drawable.avatar));
        chatMessages.add(new Chat("Merry An.", "Are you ready for today’s party?", "12 mar", R.drawable.avatar));
        chatMessages.add(new Chat("John Walton", "I’m sending you a parcel receipt.", "12 mar", R.drawable.avatar));
        chatMessages.add(new Chat("InnoXent Jay", "Let’s get back to work, You...", "12 mar", R.drawable.avatar));

        adapter = new Activity_MessagesAdapter(this, chatMessages, new Activity_MessagesAdapter.OnChatClickListener() {
            @Override
            public void onChatClick(Chat chat) {
                Intent intent = new Intent(Activity_Messages_Listing.this, Activity_Chat_Conversation.class);
                intent.putExtra("name", chat.getName()); // Optional: pass data
                startActivity(intent);
            }
        });

        chatRecyclerView.setAdapter(adapter);
    }
}
