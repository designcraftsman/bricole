package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.emsi.bricole_app.R;

public class Activity_Application_Details extends Drawer {

    private SharedPreferences prefs;
    private String USER_ACCESS_TOKEN;

    private int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_application_details);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);
        user_id = prefs.getInt("user_id",-1);

        // Get applicationId from Intent
        int applicationId = getIntent().getIntExtra("application_id", -1);

        if (applicationId != -1) {
            // Fetch the application details from API
            fetchApplicationDetails(applicationId);
        }
    }

    private void fetchApplicationDetails(int applicationId) {
        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected JSONObject doInBackground(Void... voids) {
                try {
                    URL url = new URL("http://10.0.2.2:8080/api/employee/application/" + applicationId); // use 10.0.2.2 for localhost in emulator
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Authorization", "Bearer " + USER_ACCESS_TOKEN);

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String inputLine;
                        StringBuilder response = new StringBuilder();

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        return jsonResponse.getJSONObject("data");
                    }
                } catch (Exception e) {
                    Log.e("API_ERROR", e.toString());
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONObject data) {
                if (data != null) {
                    updateUI(data);
                }
            }
        }.execute();
    }

    private void updateUI(JSONObject data) {
        try {
            TextView txtTitle = findViewById(R.id.txtTitle);
            TextView txtLocation = findViewById(R.id.txtLocation);
            TextView txtDescription = findViewById(R.id.txtDescription);
            TextView txtPostedBy = findViewById(R.id.txtPostedBy);
            TextView txtContact = findViewById(R.id.txtContact);
            TextView txtStatus = findViewById(R.id.txtStatus);

            txtTitle.setText(data.getString("title"));
            txtLocation.setText(data.getString("location"));
            txtDescription.setText(data.getString("description"));

            // ✅ Get the applicant's status based on user_id
            JSONObject applicants = data.getJSONObject("applicants");
            String applicationStatus = applicants.optString(String.valueOf(user_id), "Unknown");
            txtStatus.setText(applicationStatus);

            // Hardcode posted by and contact (for now)
            txtPostedBy.setText("Offre reçue");
            txtContact.setText("📞 Contact : 0652976002");

            // Missions
            JSONArray missions = data.getJSONArray("missions");
            if (missions.length() > 0) {
                TextView txtMission1 = findViewById(R.id.txtMission1);
                txtMission1.setText("✔️ " + missions.getString(0));
            }
            if (missions.length() > 1) {
                TextView txtMission2 = findViewById(R.id.txtMission2);
                txtMission2.setText("✔️ " + missions.getString(1));
            }
            if (missions.length() > 2) {
                TextView txtMission3 = findViewById(R.id.txtMission3);
                txtMission3.setText("✔️ " + missions.getString(2));
            }

        } catch (Exception e) {
            Log.e("UI_UPDATE_ERROR", e.toString());
        }
    }

}
