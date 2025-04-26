package com.emsi.bricole_app.controllers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.emsi.bricole_app.R;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity_Profile_Employee_Availability extends Drawer {

    private SharedPreferences prefs;
    private String USER_ACCESS_TOKEN;

    private Spinner spinnerMonday, spinnerTuesday, spinnerWednesday, spinnerThursday, spinnerFriday, spinnerSaturday, spinnerSunday;
    private Button buttonSaveAvailability;

    private final String[] availabilityOptions = {"FULLTIME", "PARTTIME", "UNAVAILABLE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupDrawer(R.layout.activity_profile_employee_availability);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        spinnerMonday = findViewById(R.id.spinnerMonday);
        spinnerTuesday = findViewById(R.id.spinnerTuesday);
        spinnerWednesday = findViewById(R.id.spinnerWednesday);
        spinnerThursday = findViewById(R.id.spinnerThursday);
        spinnerFriday = findViewById(R.id.spinnerFriday);
        spinnerSaturday = findViewById(R.id.spinnerSaturday);
        spinnerSunday = findViewById(R.id.spinnerSunday);
        buttonSaveAvailability = findViewById(R.id.buttonSaveAvailability);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, availabilityOptions);

        spinnerMonday.setAdapter(adapter);
        spinnerTuesday.setAdapter(adapter);
        spinnerWednesday.setAdapter(adapter);
        spinnerThursday.setAdapter(adapter);
        spinnerFriday.setAdapter(adapter);
        spinnerSaturday.setAdapter(adapter);
        spinnerSunday.setAdapter(adapter);

        buttonSaveAvailability.setOnClickListener(view -> sendAvailability());
    }

    private void sendAvailability() {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2:8080/api/employee/profile/update/availabilty");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + USER_ACCESS_TOKEN);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("mondayAvailability", spinnerMonday.getSelectedItem().toString());
                jsonBody.put("tuesdayAvailability", spinnerTuesday.getSelectedItem().toString());
                jsonBody.put("wednesdayAvailability", spinnerWednesday.getSelectedItem().toString());
                jsonBody.put("thursdayAvailability", spinnerThursday.getSelectedItem().toString());
                jsonBody.put("fridayAvailability", spinnerFriday.getSelectedItem().toString());
                jsonBody.put("saturdayAvailability", spinnerSaturday.getSelectedItem().toString());
                jsonBody.put("sundayAvailability", spinnerSunday.getSelectedItem().toString());

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    runOnUiThread(() -> Toast.makeText(this, "Disponibilité mise à jour avec succès !", Toast.LENGTH_SHORT).show());
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
