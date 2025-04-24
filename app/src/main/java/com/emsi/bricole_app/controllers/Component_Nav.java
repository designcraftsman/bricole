package com.emsi.bricole_app.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Component_Nav {

    private static final String PROFILE_DATA_URL = "http://10.0.2.2:8080/api/profile";

    public static void loadUserProfileImage(Context context, ImageView imageView) {
        SharedPreferences prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        final String token = prefs.getString("access_token", null);

        if (token == null) return;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PROFILE_DATA_URL, null,
                response -> {
                    try {
                        String profilePicture = response.getString("profilePicture");
                        String imageUrl = "http://10.0.2.2:8080/images/profile/" + URLEncoder.encode(profilePicture, "UTF-8");

                        Glide.with(context)
                                .load(imageUrl)
                                .into(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
