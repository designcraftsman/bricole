package com.emsi.bricole_app.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Notifications extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<Notification> notificationList;
    private static final String API_URL = "http://10.0.2.2:8080/api/user/notifications"; // 10.0.2.2 for Android Emulator
    private SharedPreferences prefs;
    private String USER_ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.notificationRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        notificationList = new ArrayList<>();
        adapter = new NotificationsAdapter(notificationList, (notificationId, position) -> {
            deleteNotification(notificationId, position);
        });

        recyclerView.setAdapter(adapter);

        fetchNotifications();
    }

    private void fetchNotifications() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                response -> {
                    try {
                        JSONArray dataArray = response.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject obj = dataArray.getJSONObject(i);
                            int id = obj.getInt("notificationId");
                            int senderId = obj.getInt("senderId");
                            String message = obj.getString("message");
                            String sentAt = obj.getString("sentAt");

                            notificationList.add(new Notification(id, senderId, message, sentAt));
                        }

                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing notifications", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to fetch notifications", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + USER_ACCESS_TOKEN);
                return headers;
            }
        };

        queue.add(request);
    }

    private void deleteNotification(int notificationId, int position) {
        String deleteUrl = API_URL + "/" + notificationId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, deleteUrl, null,
                response -> {
                    Toast.makeText(this, "Notification deleted", Toast.LENGTH_SHORT).show();
                    adapter.removeItem(position);
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Failed to delete notification", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + USER_ACCESS_TOKEN);
                return headers;
            }
        };

        queue.add(request);
    }


}
