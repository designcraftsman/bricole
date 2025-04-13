package com.emsi.bricole_app.controllers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.emsi.bricole_app.R;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.*;

public class Activity_Signup_Form extends AppCompatActivity {

    private EditText firstNameField, lastNameField, phoneField, emailField, passwordField;
    private Button mBtnSignup;
    private static final String BASE_URL = "http://10.0.2.2:8080/api/auth/employee/register"; // Replace with your API endpoint

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);

        firstNameField = findViewById(R.id.First_Name);
        lastNameField = findViewById(R.id.Last_Name);
        phoneField = findViewById(R.id.Phone);
        emailField = findViewById(R.id.etEmail);
        passwordField = findViewById(R.id.etPassword);

        mBtnSignup = findViewById(R.id.btnSignup);
        mBtnSignup.setOnClickListener(view -> sendSignupData());
    }

    private void sendSignupData() {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject json = new JSONObject();
            json.put("firstname", firstNameField.getText().toString());
            json.put("lastname", lastNameField.getText().toString());
            json.put("phoneNumberPrefix", 212); // Assuming Morocco
            json.put("phoneNumber", phoneField.getText().toString());
            json.put("address", "Default Address"); // You can make this dynamic
            json.put("gender", "MALE"); // Or get from user input
            json.put("profilePicture", "https://example.com/profile.jpg");
            json.put("email", emailField.getText().toString());
            json.put("password", passwordField.getText().toString());
            json.put("skills", new org.json.JSONArray().put("PAINTING"));
            json.put("jobPreferences", new org.json.JSONArray().put("PAINTING"));

            JSONObject availability = new JSONObject();
            availability.put("Monday", "FULLTIME");
            availability.put("Tuesday", "FULLTIME");
            availability.put("Wednesday", "FULLTIME");
            availability.put("Thursday", "FULLTIME");
            availability.put("Friday", "FULLTIME");
            availability.put("Saturday", "FULLTIME");
            availability.put("Sunday", "FULLTIME");

            json.put("availability", availability);

            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

            Request request = new Request.Builder()
                    .url(BASE_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace(); // Or show a Toast
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        // Handle success (maybe navigate to login?)
                        System.out.println("Signup successful: " + response.body().string());
                    } else {
                        System.out.println("Signup failed: " + response.code());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
