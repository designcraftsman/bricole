package com.emsi.bricole_app.controllers;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.emsi.bricole_app.R;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.*;

public class Activity_Signup_Form extends AppCompatActivity {

    private EditText firstNameField, lastNameField, phonePrefixField,phoneField, emailField, passwordField,addressField,birthDateField;
    private Button mBtnSignup;

    private AutoCompleteTextView mGenderOptions;

    private TextInputLayout mGender;
    private static final String EMPLOYEE_URL = "http://10.0.2.2:8080/api/auth/employee/register"; // Replace with your API endpoint
    private static final String EMPLOYER_URL = "http://10.0.2.2:8080/api/auth/employer/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);
        firstNameField = findViewById(R.id.First_Name);
        lastNameField = findViewById(R.id.Last_Name);
        phonePrefixField = findViewById(R.id.Phone_Prefix);
        phoneField = findViewById(R.id.Phone);
        emailField = findViewById(R.id.etEmail);
        passwordField = findViewById(R.id.etPassword);
        addressField = findViewById(R.id.Adress);
        mBtnSignup = findViewById(R.id.btnSignup);
        mGender = findViewById(R.id.Gender);
        mGenderOptions = findViewById(R.id.Gender_Options);
        birthDateField = findViewById(R.id.Birth_Date);

        mGender.setHint("genre");

        mGender.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_Signup_Form.class);
            startActivity(intent);
        });


        String[] cities = {"MALE","FEMALE","autre"};

        ArrayAdapter<String> adapter =  new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,cities);

        mGenderOptions.setAdapter(adapter);

        EditText birthDateField = findViewById(R.id.Birth_Date);

        birthDateField.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_Signup_Form.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        // Format date and set it to EditText
                        String formattedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        birthDateField.setText(formattedDate);
                    }, year, month, day);

            datePickerDialog.show();

        });


        String userType = getIntent().getStringExtra("user_type");


        mBtnSignup.setOnClickListener(view -> {
            if ("employee".equalsIgnoreCase(userType)) {
                signupEmployee();
            } else if ("employer".equalsIgnoreCase(userType)) {
                signupEmployer();
            } else {
                // Optional fallback
                Toast.makeText(this, "Unknown user type!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signupEmployee() {
        OkHttpClient client = new OkHttpClient();
        try {
            JSONObject json = new JSONObject();
            json.put("firstname", firstNameField.getText().toString());
            json.put("lastname", lastNameField.getText().toString());
            json.put("phoneNumberPrefix", phonePrefixField.getText().toString()); // Assuming Morocco
            json.put("gender", mGenderOptions.getText().toString());
            json.put("phoneNumber", phoneField.getText().toString());
            json.put("address", addressField.getText().toString()); // You can make this dynamic
            json.put("gender", mGenderOptions.getText().toString()); // Or get from user input
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
                    .url(EMPLOYEE_URL)
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
                        String responseBody = response.body().string();
                        try {
                            JSONObject responseJson = new JSONObject(responseBody);
                            System.out.println("This is the response from the api:" + responseBody);
                            String accessToken = responseJson.getString("access_token");

                            // Save token to SharedPreferences
                            getSharedPreferences("auth", MODE_PRIVATE)
                                    .edit()
                                    .putString("token", accessToken)
                                    .apply();

                            System.out.println("Signup successful. Token: " + accessToken);

                            // Optionally go to another activity
                            runOnUiThread(() -> {
                                // For example: navigate to MainActivity
                                Intent intent = new Intent(Activity_Signup_Form.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Signup failed: " + response.code());
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void signupEmployer() {
        OkHttpClient client = new OkHttpClient();
        try {
            JSONObject json = new JSONObject();
            json.put("firstname", firstNameField.getText().toString());
            json.put("lastname", lastNameField.getText().toString());
            json.put("phoneNumberPrefix", phonePrefixField.getText().toString()); // Assuming Morocco
            json.put("phoneNumber", phoneField.getText().toString());
            json.put("address", addressField.getText().toString()); // You can make this dynamic
            json.put("gender", mGenderOptions.getText().toString()); // Or get from user input
            json.put("email", emailField.getText().toString());
            json.put("password", passwordField.getText().toString());


            RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json"));

            System.out.println("Payload: " + json.toString());

            Request request = new Request.Builder()
                    .url(EMPLOYER_URL)
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
                        String responseBody = response.body().string();
                        try {
                            JSONObject responseJson = new JSONObject(responseBody);
                            System.out.println("This is the response from the api:" + responseBody);
                            String accessToken = responseJson.getString("access_token");

                            // Save token to SharedPreferences
                            getSharedPreferences("auth", MODE_PRIVATE)
                                    .edit()
                                    .putString("token", accessToken)
                                    .apply();

                            System.out.println("Signup successful. Token: " + accessToken);

                            // Optionally go to another activity
                            runOnUiThread(() -> {
                                // For example: navigate to MainActivity
                                Intent intent = new Intent(Activity_Signup_Form.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Signup failed: " + response.code() + " - " + response.body().string());

                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
