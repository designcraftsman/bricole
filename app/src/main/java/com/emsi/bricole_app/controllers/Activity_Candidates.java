package com.emsi.bricole_app.controllers;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private JSONArray originalCandidatesArray;
    private Button acceptedFilterBtn, pendingFilterBtn, rejectedFilterBtn;
    private String selectedStatus = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_candidates);

        mTableLayoutCandidates = findViewById(R.id.table_layout_candidates);
        mNoCandidatesContainer = findViewById(R.id.no_candidates_container);
        // Get the access token from shared preferences
        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        EditText searchEditText = findViewById(R.id.search_input); // Give your EditText an ID like "search_input"

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim().toLowerCase();
                filterCandidatesByName(query);
            }
        });

        acceptedFilterBtn = findViewById(R.id.accepted_filter);
        pendingFilterBtn = findViewById(R.id.pending_filter);
        rejectedFilterBtn = findViewById(R.id.rejected_filter);

        View.OnClickListener filterClickListener = v -> {
            if (v == acceptedFilterBtn) {
                selectedStatus = "ACCEPTED";
                highlightSelectedButton(acceptedFilterBtn);
            } else if (v == pendingFilterBtn) {
                selectedStatus = "PENDING";
                highlightSelectedButton(pendingFilterBtn);
            } else if (v == rejectedFilterBtn) {
                selectedStatus = "REJECTED";
                highlightSelectedButton(rejectedFilterBtn);
            }
            filterCandidatesByName(((EditText) findViewById(R.id.search_input)).getText().toString().trim().toLowerCase());
        };

        acceptedFilterBtn.setOnClickListener(filterClickListener);
        pendingFilterBtn.setOnClickListener(filterClickListener);
        rejectedFilterBtn.setOnClickListener(filterClickListener);


        fetchCandidates();
    }

    private void highlightSelectedButton(Button selectedButton) {
        // Reset all
        acceptedFilterBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        pendingFilterBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        rejectedFilterBtn.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        // Highlight selected
        selectedButton.setBackgroundColor(getResources().getColor(R.color.orange));
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
                                originalCandidatesArray = candidatesArray;
                                displayCandidates(originalCandidatesArray);

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

    private void filterCandidatesByName(String query) {
        if (originalCandidatesArray == null) return;

        JSONArray filteredArray = new JSONArray();

        try {
            for (int i = 0; i < originalCandidatesArray.length(); i++) {
                JSONObject offer = originalCandidatesArray.getJSONObject(i);
                JSONObject job = offer.getJSONObject("job");
                JSONArray applicants = offer.optJSONArray("applicants");

                if (applicants != null) {
                    JSONArray filteredApplicants = new JSONArray();

                    for (int j = 0; j < applicants.length(); j++) {
                        JSONObject applicantObject = applicants.getJSONObject(j);
                        JSONObject employee = applicantObject.getJSONObject("employeeDTO");
                        String fullName = employee.getString("firstname") + " " + employee.getString("lastname");

                        String applicationStatus = applicantObject.getString("applicationState");

                        boolean nameMatches = fullName.toLowerCase().contains(query);
                        boolean statusMatches = (selectedStatus == null || applicationStatus.equalsIgnoreCase(selectedStatus));

                        if (nameMatches && statusMatches) {
                            filteredApplicants.put(applicantObject);
                        }

                    }

                    if (filteredApplicants.length() > 0) {
                        JSONObject newOffer = new JSONObject();
                        newOffer.put("job", job);
                        newOffer.put("applicants", filteredApplicants);
                        filteredArray.put(newOffer);
                    }
                }
            }

            displayCandidates(filteredArray);
        } catch (JSONException e) {
            Log.e(TAG, "Filtering error: " + e.getMessage());
        }
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
                        popupMenu.getMenuInflater().inflate(R.menu.menu_candidate_options, popupMenu.getMenu());

                        popupMenu.setOnMenuItemClickListener(item -> {
                            int itemId = item.getItemId();

                            if (itemId == R.id.menu_view) {
                                Intent viewIntent = new Intent(Activity_Candidates.this, Activity_Application_View.class); // example activity
                                viewIntent.putExtra("job_id", finalJobId);
                                viewIntent.putExtra("employee_id", finalEmployeeId);
                                viewIntent.putExtra("employee_name", fullName);
                                startActivity(viewIntent);
                                return true;
                            } else if (itemId == R.id.menu_edit) {
                                Intent editIntent = new Intent(Activity_Candidates.this, Activity_Job_Details.class);
                                editIntent.putExtra("job_id", finalJobId);
                                startActivity(editIntent);
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

}
