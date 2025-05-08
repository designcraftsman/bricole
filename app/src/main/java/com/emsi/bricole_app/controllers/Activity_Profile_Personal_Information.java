package com.emsi.bricole_app.controllers;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.emsi.bricole_app.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;

import okhttp3.*;

public class Activity_Profile_Personal_Information extends Drawer {

    private EditText firstNameField, lastNameField, phonePrefixField,phoneField, addressField;
    private Button mBtnUpdate;
    private AutoCompleteTextView mGenderOptions;

    private TextInputLayout mGender;
    private final String PROFILE_DATA_URL = "http://10.0.2.2:8080/api/profile";
    private static final String API_URL = "http://10.0.2.2:8080/api/profile/update";
    private SharedPreferences prefs;
    private String USER_ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_profile_personal_information);
        firstNameField = findViewById(R.id.First_Name);
        lastNameField = findViewById(R.id.Last_Name);
        phonePrefixField = findViewById(R.id.Phone_Prefix);
        phoneField = findViewById(R.id.Phone);
        addressField = findViewById(R.id.Adress);
        mBtnUpdate = findViewById(R.id.buttonUpdate);
        mGender = findViewById(R.id.Gender);
        mGenderOptions = findViewById(R.id.Gender_Options);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);


        // Category dropdown setup
        String[] gender_options = {"MALE", "FEMALE", "OTHER"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, gender_options);
        mGenderOptions.setAdapter(adapter);
        mGenderOptions.setOnClickListener(v -> mGenderOptions.showDropDown());
        mGenderOptions.setFocusable(false);
        mGenderOptions.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mGenderOptions.setInputType(0);


        fetchUserProfile(USER_ACCESS_TOKEN);

        mBtnUpdate.setOnClickListener(view -> {
                updateProfile();
        });
    }

    private void fetchUserProfile(String token) {
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, PROFILE_DATA_URL, null,
                response -> {
                    try {
                        firstNameField.setText(response.getString("firstname"));
                        lastNameField.setText(response.getString("lastname"));
                        phonePrefixField.setText(String.valueOf(response.getInt("phoneNumberPrefix")));
                        phoneField.setText(response.getString("phoneNumber"));
                        addressField.setText(response.getString("address"));
                        mGenderOptions.setText(response.getString("gender"), false);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }



    private void updateProfile() {
        OkHttpClient client = new OkHttpClient();
        try {
            JSONObject json = new JSONObject();
            json.put("firstname", firstNameField.getText().toString());
            json.put("lastname", lastNameField.getText().toString());
            json.put("phoneNumberPrefix", phonePrefixField.getText().toString()); // Assuming Morocco
            json.put("phoneNumber", phoneField.getText().toString());
            json.put("address", addressField.getText().toString()); // You can make this dynamic
            json.put("gender", mGenderOptions.getText().toString()); // Or get from user input



            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));


            Request request = new Request.Builder()
                    .url(API_URL)
                    .put(body)
                    .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                    .build();


            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace(); // Or show a Toast
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        try {
                            JSONObject responseJson = new JSONObject(responseBody);
                            System.out.println("Update successful");
                            runOnUiThread(() -> {
                                Toast.makeText(Activity_Profile_Personal_Information.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Update failed: " + response.code() + " - " + response.body().string());
                        runOnUiThread(() -> {
                            Toast.makeText(Activity_Profile_Personal_Information.this, "Error updating profile", Toast.LENGTH_SHORT).show();
                        });

                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
