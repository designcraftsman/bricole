package com.emsi.bricole_app.controllers;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.emsi.bricole_app.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Activity_Profile_Skills extends Drawer {
    private String USER_ACCESS_TOKEN;
    private SharedPreferences prefs;
    private ListView listViewJobSkills;
    private Button buttonSubmitSkills;

    // Fixed skills
    private final String[] skillsList = {
            "PLUMBING",
            "ELECTRICAL",
            "CARPENTRY",
            "PAINTING",
            "GARDENING",
            "CLEANING",
            "MOVING",
            "OTHER"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_profile_skills);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        listViewJobSkills = findViewById(R.id.listViewJobSkills);
        buttonSubmitSkills = findViewById(R.id.buttonSubmitSkills);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, skillsList);
        listViewJobSkills.setAdapter(adapter);

        buttonSubmitSkills.setOnClickListener(v -> submitSelectedSkills());
    }

    private void submitSelectedSkills() {
        List<String> selectedSkills = new ArrayList<>();

        for (int i = 0; i < listViewJobSkills.getCount(); i++) {
            if (listViewJobSkills.isItemChecked(i)) {
                selectedSkills.add(skillsList[i]);
            }
        }

        if (selectedSkills.isEmpty()) {
            Toast.makeText(this, "Veuillez sélectionner au moins une compétence.", Toast.LENGTH_SHORT).show();
            return;
        }

        sendSkillUpdate(selectedSkills);
    }

    private void sendSkillUpdate(List<String> selectedSkills) {
        try {
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://10.0.2.2:8080/api/employee/profile/update/skills";

            JSONObject body = new JSONObject();
            body.put("skills", new JSONArray(selectedSkills));

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST, url, body,
                    response -> Toast.makeText(this, "Mise à jour réussie!", Toast.LENGTH_SHORT).show(),
                    error -> Toast.makeText(this, "Erreur: " + error.getMessage(), Toast.LENGTH_LONG).show()
            ) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    java.util.Map<String, String> headers = new java.util.HashMap<>();
                    headers.put("Authorization", "Bearer " + USER_ACCESS_TOKEN);
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            queue.add(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
