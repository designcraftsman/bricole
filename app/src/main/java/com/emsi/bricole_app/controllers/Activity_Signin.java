package com.emsi.bricole_app.controllers;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.emsi.bricole_app.R;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_Signin extends AppCompatActivity {

    private Button mBtnLogin;
    private TextView mBtnSignupRedirect;

    private EditText mEmailInput;

    final static String BASE_URL="http://10.0.2.2:8080/api/auth/login";

    private EditText mPasswordInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // Set the correct layout
        mBtnSignupRedirect = findViewById(R.id.btnSignupRedirect);

        mEmailInput = findViewById(R.id.email_input);

        mPasswordInput = findViewById(R.id.password_input);

        mBtnLogin = findViewById(R.id.btnLogin);

        mBtnLogin.setOnClickListener(view->{
            String email = mEmailInput.getText().toString().trim();
            String password = mPasswordInput.getText().toString().trim();

            if (email.isEmpty()) {
                mEmailInput.setError("Email is required");
                mEmailInput.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                mPasswordInput.setError("Password is required");
                mPasswordInput.requestFocus();
                return;
            }
            this.sendSigninData();

        });

        mBtnSignupRedirect.setOnClickListener(view->{
            Intent intent = new Intent(this , MainActivity.class);
            startActivity(intent);
        });
    }

    private void sendSigninData() {
        OkHttpClient client = new OkHttpClient();

        String email = mEmailInput.getText().toString().trim();
        String password = mPasswordInput.getText().toString().trim();

        // Build the URL with query parameters
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("email", email)
                .addQueryParameter("password", password)
                .build();

        // Create empty body since backend expects POST but reads only URL params
        RequestBody body = RequestBody.create("", null);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject responseJson = new JSONObject(responseBody);

                        String accessToken = responseJson.getString("access_token");
                        String refreshToken = responseJson.getString("refresh_token");
                        int userId = responseJson.getInt("userId");
                        String fullName = responseJson.getString("fullName");
                        String role = responseJson.getString("role");

                        // Save data to SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit()
                                .putString("access_token", accessToken)
                                .putString("refresh_token", refreshToken)
                                .putInt("user_id", userId)
                                .putString("fullName", fullName)
                                .putString("role", role)
                                .apply();

                        runOnUiThread(() -> {
                            Intent intent;

                            if (role.equalsIgnoreCase("employer")) {
                                intent = new Intent(Activity_Signin.this, Activity_Offers.class);
                            } else if (role.equalsIgnoreCase("employee")) {
                                intent = new Intent(Activity_Signin.this, Activity_Job_Listing.class);
                            } else{
                                return;
                            }
                            startActivity(intent);
                            finish();
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Sign in failed: " + response.code());
                }
            }
        });
    }

}

