package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.emsi.bricole_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Activity_Application_View extends Drawer{
    private int user_id;
    private int job_id;
    private final String JOB_URL = "http://10.0.2.2:8080/api/main/jobs/search/";
    private SharedPreferences prefs;
    private TextView txtPostedBy, txtTitle, txtLocation, txtMission1, txtMission2, txtMission3, txtDescription,txtStatus,txtEmployeeName;
    private String USER_ACCESS_TOKEN, employee_name;
    private Button acceptBtn,rejectBtn;
    private static final String TAG = "Activity_Application_View";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_view_application);
        // Get views
        txtEmployeeName = findViewById(R.id.txtEmployeeName);
        txtTitle = findViewById(R.id.txtTitle);
        txtLocation = findViewById(R.id.txtLocation);
        txtMission1 = findViewById(R.id.txtMission1);
        txtMission2 = findViewById(R.id.txtMission2);
        txtMission3 = findViewById(R.id.txtMission3);
        txtDescription = findViewById(R.id.txtDescription);
        txtStatus = findViewById(R.id.txtStatus);
        acceptBtn = findViewById(R.id.acceptBtn);
        rejectBtn = findViewById(R.id.rejectBtn);

        Intent intent = getIntent();

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);
        user_id = intent.getIntExtra("employee_id",-1);
        job_id = intent.getIntExtra("job_id", -1);
        employee_name = intent.getStringExtra("employee_name");

        txtEmployeeName.setText(employee_name != null ? employee_name : "Unknown Employee");

        txtEmployeeName.setPaintFlags(txtEmployeeName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        txtEmployeeName.setOnClickListener(view->{
            Intent intent1 = new Intent(this, Activity_Employee_Profile_Details.class);
            intent1.putExtra("employee_id",user_id);
            startActivity(intent1);
        });

        acceptBtn.setOnClickListener(view -> {
            updateApplicationStatus("ACCEPTED"); // Assuming accepting means work starts
        });

        rejectBtn.setOnClickListener(view -> {
            updateApplicationStatus("REJECTED"); // Assuming rejecting means cancelling the application
        });


        if (job_id != -1) {
            // Fetch the application details from API
            fetchJobDetails(job_id);
        }
    }

    private void fetchJobDetails(int jobId) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(JOB_URL + jobId)
                .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Job details API call failed: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jobJson = response.body().string();
                    Log.d(TAG, "Job JSON: " + jobJson);

                    runOnUiThread(() -> {
                        try {
                            JSONObject job = new JSONObject(jobJson);

                            String title = job.getString("title");
                            String location = job.getString("location");
                            String description = job.getString("description");
                            String status = job.getString("status");

                            JSONArray missions = job.getJSONArray("missions");

                            txtTitle.setText(title);
                            txtLocation.setText(location);
                            txtDescription.setText(description);
                            txtStatus.setText(status);


                            // Display missions
                            if (missions.length() > 0) txtMission1.setText("✔️ " + missions.getString(0));
                            if (missions.length() > 1) txtMission2.setText("✔️ " + missions.getString(1));
                            if (missions.length() > 2) txtMission3.setText("✔️ " + missions.getString(2));

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });

                } else {
                    Log.e(TAG, "Job details API response not successful: Code " + response.code());
                }
            }
        });
    }

    private void updateApplicationStatus(String newStatus) {
        OkHttpClient client = new OkHttpClient();

        String url = "http://10.0.2.2:8080/api/employer/job/" + job_id + "/application/" + user_id + "/updateStatus";

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonBody = "\"" + newStatus + "\""; // Because ApplicationState is an Enum String (not an object)

        RequestBody body = RequestBody.create(jsonBody, JSON);

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Status update API call failed: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(Activity_Application_View.this, "Failed to update status", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Activity_Application_View.this, "Status updated successfully", Toast.LENGTH_SHORT).show();
                        fetchJobDetails(job_id); // Refresh details after update
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(Activity_Application_View.this, "Failed to update status: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                    Log.e(TAG, "Status update API error: " + response.body().string());
                }
            }
        });
    }


}
