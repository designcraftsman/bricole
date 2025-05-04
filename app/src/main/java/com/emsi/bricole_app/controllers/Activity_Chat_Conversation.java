package com.emsi.bricole_app.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.ChatMessage;
import com.emsi.bricole_app.models.ChatMessageDTO;
import com.emsi.bricole_app.models.ChatMessageResponseDTO;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

public class Activity_Chat_Conversation extends Drawer {
    private WebSocket webSocket;

    private ImageButton mBackBtn;
    private int receiver_id;
    private SharedPreferences prefs;
    private ImageView user2_image;
    private static final String IMAGE_BASE_URL = "http://10.0.2.2:8080/images/profile/";
    private TextView user2_name;
    private String USER_ACCESS_TOKEN;
    private ImageView sendButton;
    private RecyclerView chatRecyclerView;
    private EditText messageEditText;
    private Activity_ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessageList = new ArrayList<>();
    private int senderId;
    private int conversationId;
    private StompClient stompClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_chat_conversation);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        user2_image = findViewById(R.id.user2_image);
        user2_name = findViewById(R.id.user2_name);
        sendButton = findViewById(R.id.send_button);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageInput);
        chatAdapter = new Activity_ChatAdapter(chatMessageList);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        senderId = prefs.getInt("user_id", -1); // assuming it's stored in SharedPreferences

        user2_name.setText(getIntent().getStringExtra("user2_name"));
        String profilePictureUrl = getIntent().getStringExtra("user2_image");

        try {
            String imageUrl = IMAGE_BASE_URL + URLEncoder.encode(profilePictureUrl, "UTF-8") + "?t=" + System.currentTimeMillis();
            Glide.with(this).load(imageUrl).placeholder(R.drawable.avatar).into(user2_image);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mBackBtn = findViewById(R.id.backButton);
        mBackBtn.setOnClickListener(view -> finish());

        receiver_id = getIntent().getIntExtra("receiver_id", -1);

        boolean isNewConversation = getIntent().getBooleanExtra("isNewConversation", false);  // Retrieve flag

        if (receiver_id != -1) {
            if (isNewConversation) {
                startConversation(receiver_id);
            } else {
                conversationId = getIntent().getIntExtra("conversation_id", -1);
                loadConversation(conversationId);
                initializeWebSocket();
            }
        }

        sendButton.setOnClickListener(v -> {
            String messageText = messageEditText.getText().toString().trim();
            if (!messageText.isEmpty() && stompClient != null && stompClient.isConnected()) {
                ChatMessageDTO dto = new ChatMessageDTO();
                dto.setSenderId(senderId);
                dto.setReceiverId(receiver_id);
                dto.setRoom(String.valueOf(conversationId));
                dto.setContent(messageText);
                dto.setSenderFirstName("Me");

                // Add to UI immediately (optimistic update)
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSenderId(senderId);
                chatMessage.setContent(messageText);
                chatMessage.setType(ChatMessage.TYPE_SENT);

                runOnUiThread(() -> {
                    chatMessageList.add(chatMessage);
                    chatAdapter.notifyItemInserted(chatMessageList.size() - 1);
                    chatRecyclerView.scrollToPosition(chatMessageList.size() - 1);
                    messageEditText.setText("");
                });

                // Send via WebSocket
                String messageJson = new Gson().toJson(dto);
                stompClient.send("/app/chat.sendMessage", messageJson).subscribe();
            }
        });


    }


    public void addMessage(ChatMessage message) {
        chatMessageList.add(message);
        chatAdapter.notifyItemInserted(chatMessageList.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessageList.size() - 1);
    }


    private void initializeWebSocket() {
        String socketUrl = "ws://10.0.2.2:8080/ws?token=" + USER_ACCESS_TOKEN;
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN));

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, socketUrl);

        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("WebSocket", "WebSocket opened successfully");
                    // Only enable send button after connection is established
                    sendButton.setEnabled(true);
                    subscribeToConversation();
                    break;
                case ERROR:
                    Log.e("WebSocket", "Error: " + lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d("WebSocket", "Connection closed");
                    break;
            }
        });

        stompClient.connect(headers);
    }

    private void subscribeToConversation() {
        stompClient.topic("/topic/room/" + conversationId)
                .subscribe((StompMessage topicMessage) -> {
                    String payload = topicMessage.getPayload();
                    Log.d("WebSocket", "Received message: " + payload);

                    ChatMessageResponseDTO response = new Gson().fromJson(payload, ChatMessageResponseDTO.class);

                    runOnUiThread(() -> {
                        ChatMessage message = new ChatMessage();
                        message.setSenderId(response.getSenderId());
                        message.setContent(response.getContent());
                        if (response.getSenderId() == senderId) {
                            // Optionally skip adding own message (already added in sendButton.onClick)
                            return;
                        }
                        message.setType(ChatMessage.TYPE_RECEIVED);


                        addMessage(message);
                    });
                }, throwable -> {
                    Log.e("WebSocket", "Subscription error: " + throwable.getMessage());
                });
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
                    conversationId = response.optInt("id");
                    initializeWebSocket(); // move this here
                },
                error -> {
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

    private void loadConversation(int conversationId) {
        String jwtToken = "Bearer " + USER_ACCESS_TOKEN;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                "http://10.0.2.2:8080/api/chat/conversation/" + conversationId,
                null,
                response -> {
                    try {
                        Gson gson = new Gson();
                        JSONArray messagesArray = response.getJSONArray("messages");

                        for (int i = 0; i < messagesArray.length(); i++) {
                            JSONObject messageObj = messagesArray.getJSONObject(i);
                            ChatMessageResponseDTO msgDto = gson.fromJson(messageObj.toString(), ChatMessageResponseDTO.class);
                            System.out.println("chat message response dto is " + msgDto);
                            ChatMessage message = new ChatMessage();
                            message.setSenderId(msgDto.getSenderId());
                            message.setContent(msgDto.getContent());
                            message.setType(msgDto.getSenderId() == senderId ? ChatMessage.TYPE_SENT : ChatMessage.TYPE_RECEIVED);
                            chatMessageList.add(message);
                        }

                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(chatMessageList.size() - 1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("Chat", "Conversation load error: " + error.toString());
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
