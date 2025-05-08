package com.emsi.bricole_app.controllers;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.emsi.bricole_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Activity_Candidates extends Drawer {

    private final String TAG = "Activity_Candidates";
    private final String API_URL = "http://10.0.2.2:8080/api/employer/allApplicants";

    private SharedPreferences prefs;
    private String USER_ACCESS_TOKEN;
    private TableLayout mTableLayoutCandidates;
    private LinearLayout mNoCandidatesContainer;
    private int job_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_candidates);

        mTableLayoutCandidates = findViewById(R.id.table_layout_candidates);
        mNoCandidatesContainer = findViewById(R.id.no_candidates_container);
        // Get the access token from shared preferences
        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        fetchCandidates();
    }

    private void fetchCandidates() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String jsonData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONArray candidatesArray = new JSONArray(jsonData);
                            if(candidatesArray.length() == 0){
                                mTableLayoutCandidates.setVisibility(View.GONE);
                            }else{
                                mNoCandidatesContainer.setVisibility(View.GONE);
                                displayCandidates(candidatesArray);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "API response not successful: Code " + response.code());
                }
            }
        });
    }

    private void displayCandidates(JSONArray offersArray) throws JSONException {
        TableLayout tableLayout = findViewById(R.id.table_layout_candidates);

        // Clear existing rows except header
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }

        for (int i = 0; i < offersArray.length(); i++) {
            JSONObject offer = offersArray.getJSONObject(i);
            String jobTitle = offer.getJSONObject("job").getString("title");

            JSONArray applicants = offer.optJSONArray("applicants");
            if (applicants != null) {
                for (int j = 0; j < applicants.length(); j++) {
                    JSONObject applicantObject = applicants.getJSONObject(j);

                    JSONObject employee = applicantObject.getJSONObject("employeeDTO");

                    String fullName = employee.getString("firstname") + " " + employee.getString("lastname");
                    String status = applicantObject.getString("applicationState");

                    // Inflate a new TableRow
                    TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.component_candidates_row, null);

                    // Set name
                    TextView nameView = row.findViewById(R.id.candidates_name);
                    nameView.setText(fullName);

                    // Set job title
                    TextView jobTitleView = row.findViewById(R.id.candidates_jobOffer);
                    jobTitleView.setText(jobTitle);

                    // Set status
                    TextView statusView = row.findViewById(R.id.candidates_status);
                    statusView.setText(status);

                    ImageView moreBtn = row.findViewById(R.id.candidates_more_button);

                    int finalJobId = offer.getJSONObject("job").getInt("id");
                    int finalEmployeeId = employee.getInt("id");

                    moreBtn.setOnClickListener(v -> {
                        PopupMenu popupMenu = new PopupMenu(Activity_Candidates.this, v);
                        popupMenu.getMenuInflater().inflate(R.menu.menu_offer_options, popupMenu.getMenu());

                        popupMenu.setOnMenuItemClickListener(item -> {
                            int itemId = item.getItemId();

                            if (itemId == R.id.menu_view) {
                                System.out.println("the id being sent is :" + finalJobId);
                                Intent viewIntent = new Intent(Activity_Candidates.this, Activity_Application_View.class); // example activity
                                viewIntent.putExtra("job_id", finalJobId);
                                viewIntent.putExtra("employee_id", finalEmployeeId);
                                viewIntent.putExtra("employee_name", fullName);
                                startActivity(viewIntent);
                                return true;
                            } else if (itemId == R.id.menu_edit) {
                                Intent editIntent = new Intent(Activity_Candidates.this, Activity_EditJobOffer.class);
                                editIntent.putExtra("job_id", finalJobId);
                                startActivity(editIntent);
                                return true;
                            } else if (itemId == R.id.menu_delete) {
                                deleteApplication(finalEmployeeId); // Assuming delete by employee/applicant
                                return true;
                            }

                            return false;
                        });

                        popupMenu.show();
                    });

                    // Add the row to the table
                    tableLayout.addView(row);
                }
            }
        }
    }
    private void deleteApplication(int offerId) {
        // Call your API to delete the offer and refresh the list if needed
        Log.d(TAG, "Deleted application ID: " + offerId);
        // You can add a confirmation dialog and API call here
    }

}
