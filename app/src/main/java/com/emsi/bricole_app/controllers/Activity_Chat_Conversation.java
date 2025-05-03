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
            if (!messageText.isEmpty()) {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setSenderId(senderId);
                chatMessage.setReceiverId(receiver_id);
                chatMessage.setContent(messageText);
                chatMessage.setConversationId(conversationId);
                chatMessage.setType(ChatMessage.TYPE_SENT);

                // Add immediately to UI
                chatMessageList.add(chatMessage);
                chatAdapter.notifyItemInserted(chatMessageList.size() - 1);
                chatRecyclerView.scrollToPosition(chatMessageList.size() - 1);
                messageEditText.setText("");

                // Log the message being sent
                Log.d("WebSocket", "Sending message: " + messageText);

                // Build DTO to send
                ChatMessageDTO dto = new ChatMessageDTO();
                dto.setSenderId(senderId);
                dto.setReceiverId(receiver_id);
                dto.setRoom(String.valueOf(conversationId));
                dto.setContent(messageText);
                dto.setSenderFirstName("Me");  // Optional

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
        // Create the headers list with Authorization header
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN));

        // Initialize and connect with headers
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, socketUrl);
        stompClient.connect(headers);


        // Debugging - Log WebSocket Connection events
        stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.d("WebSocket", "WebSocket opened successfully.");
                    break;
                case ERROR:
                    Log.e("WebSocket", "WebSocket connection error: " + lifecycleEvent.getException());
                    break;
                case CLOSED:
                    Log.d("WebSocket", "WebSocket connection closed: " + lifecycleEvent.getMessage());
                    break;
            }
        });

        // Debugging - Subscribe to topic messages
        stompClient.topic("/topic/room/" + conversationId)
                .subscribe((StompMessage topicMessage) -> {
                    String payload = topicMessage.getPayload();
                    Log.d("WebSocket", "Received message: " + payload); // Log the received payload

                    ChatMessageResponseDTO response = new Gson().fromJson(payload, ChatMessageResponseDTO.class);

                    ChatMessage message = new ChatMessage();
                    message.setSenderId(response.getSenderId());
                    message.setContent(response.getContent());
                    message.setType(response.getSenderId() == senderId ? ChatMessage.TYPE_SENT : ChatMessage.TYPE_RECEIVED);

                    addMessage(message);
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

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                "http://10.0.2.2:8080/api/chat/conversation/" + conversationId,
                null,
                response -> {
                    Gson gson = new Gson();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject messageObj = response.getJSONObject(i);
                            ChatMessageResponseDTO msgDto = gson.fromJson(messageObj.toString(), ChatMessageResponseDTO.class);

                            ChatMessage message = new ChatMessage();
                            message.setSenderId(msgDto.getSenderId());
                            message.setContent(msgDto.getContent());
                            message.setType(msgDto.getSenderId() == senderId ? ChatMessage.TYPE_SENT : ChatMessage.TYPE_RECEIVED);

                            chatMessageList.add(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(chatMessageList.size() - 1);
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
