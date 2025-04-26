package com.emsi.bricole_app.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.emsi.bricole_app.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Activity_Profile_Preference extends Drawer {

    private SharedPreferences prefs;
    private String USER_ACCESS_TOKEN;
    private ListView listView;
    private Button submitButton;

    private final String[] jobCategories = {
            "PLUMBING", "ELECTRICAL", "CARPENTRY", "PAINTING", "GARDENING",
            "CLEANING", "MOVING", "MAINTENANCE", "MASONRY", "OTHER"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_profile_preference);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        listView = findViewById(R.id.listViewJobPreferences);
        submitButton = findViewById(R.id.buttonSubmitPreferences);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, jobCategories);
        listView.setAdapter(adapter);

        submitButton.setOnClickListener(view -> {
            ArrayList<String> selectedPreferences = new ArrayList<>();

            for (int i = 0; i < listView.getCount(); i++) {
                if (listView.isItemChecked(i)) {
                    selectedPreferences.add(jobCategories[i]);
                }
            }

            if (!selectedPreferences.isEmpty()) {
                sendJobPreferences(selectedPreferences);
            } else {
                Toast.makeText(this, "Veuillez sélectionner au moins une préférence.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendJobPreferences(ArrayList<String> selectedPreferences) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/employee/profile/update/jobPreferences");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + USER_ACCESS_TOKEN);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                JSONArray jsonArray = new JSONArray(selectedPreferences);
                jsonBody.put("jobPreferences", jsonArray);

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> Toast.makeText(this, "Préférences mises à jour avec succès !", Toast.LENGTH_SHORT).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Erreur lors de la mise à jour.", Toast.LENGTH_SHORT).show());
                }

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
