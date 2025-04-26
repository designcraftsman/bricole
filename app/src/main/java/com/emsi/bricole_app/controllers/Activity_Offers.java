package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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

public class Activity_Offers extends Drawer {

    private final String TAG = "Activity_Offers";
    private final String API_URL = "http://10.0.2.2:8080/api/employer/job/offers";

    private SharedPreferences prefs;
    private String USER_ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_offers);

        // Get the access token from shared preferences
        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        fetchOffers();
    }

    private void fetchOffers() {
        OkHttpClient client = new OkHttpClient();

        Request offersRequest = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                .build();

        client.newCall(offersRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Offers API call failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response offersResponse) throws IOException {
                if (offersResponse.isSuccessful()) {
                    final String offersJson = offersResponse.body().string();

                    // Second request to fetch applicants
                    Request applicantsRequest = new Request.Builder()
                            .url("http://10.0.2.2:8080/api/employer/allApplicants")
                            .addHeader("Authorization", "Bearer " + USER_ACCESS_TOKEN)
                            .build();

                    client.newCall(applicantsRequest).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "Applicants API call failed: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response applicantsResponse) throws IOException {
                            if (applicantsResponse.isSuccessful()) {
                                final String applicantsJson = applicantsResponse.body().string();

                                runOnUiThread(() -> {
                                    try {
                                        JSONArray offersArray = new JSONArray(offersJson);
                                        JSONArray applicantsArray = new JSONArray(applicantsJson);

                                        // Enrich each offer with accepted/pending/rejected counts
                                        for (int i = 0; i < offersArray.length(); i++) {
                                            JSONObject offer = offersArray.getJSONObject(i);
                                            int jobId = offer.getInt("id");

                                            int accepted = 0;
                                            int pending = 0;
                                            int rejected = 0;

                                            for (int j = 0; j < applicantsArray.length(); j++) {
                                                JSONObject jobApplicantObj = applicantsArray.getJSONObject(j);
                                                JSONObject job = jobApplicantObj.getJSONObject("job");
                                                int applicantJobId = job.getInt("id");

                                                if (applicantJobId == jobId) {
                                                    JSONArray applicants = jobApplicantObj.getJSONArray("applicants");
                                                    for (int k = 0; k < applicants.length(); k++) {
                                                        JSONObject applicant = applicants.getJSONObject(k);
                                                        String state = applicant.getString("applicationState");
                                                        switch (state) {
                                                            case "PENDING": pending++; break;
                                                            case "ACCEPTED": accepted++; break;
                                                            case "REJECTED": rejected++; break;
                                                        }
                                                    }
                                                }
                                            }

                                            offer.put("pending", pending);
                                            offer.put("accepted", accepted);
                                            offer.put("rejected", rejected);
                                        }

                                        displayOffers(offersArray);

                                    } catch (JSONException e) {
                                        Log.e(TAG, "JSON parsing error: " + e.getMessage());
                                    }
                                });

                            } else {
                                Log.e(TAG, "Applicants API response not successful: Code " + applicantsResponse.code());
                            }
                        }
                    });

                } else {
                    Log.e(TAG, "Offers API response not successful: Code " + offersResponse.code());
                }
            }
        });
    }


    private void displayOffers(JSONArray offersArray) throws JSONException {
        TableLayout tableLayout = findViewById(R.id.table_layout_offers);

        // Remove existing dynamic rows (excluding the header)
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }

        for (int i = 0; i < offersArray.length(); i++) {
            JSONObject offer = offersArray.getJSONObject(i);
            int offerId = offer.getInt("id");
            String title = offer.getString("title");

            // Example mock values (replace with actual JSON values if available)
            int accepted = offer.optInt("accepted", 0);
            int pending = offer.optInt("pending", 0);
            int rejected = offer.optInt("rejected", 0);

            // Inflate a new TableRow from layout
            TableRow row = (TableRow) getLayoutInflater().inflate(R.layout.component_offers_row, null);

            // Set title
            TextView titleView = row.findViewById(R.id.offers_title);
            titleView.setText(title);

            // Set candidate stats
            TextView acceptedView = row.findViewById(R.id.offers_candidates_accepted_count);
            TextView pendingView = row.findViewById(R.id.offers_candidates_pending_count);
            TextView rejectedView = row.findViewById(R.id.offers_candidates_rejected_count);
            ImageView moreBtn = row.findViewById(R.id.offers_more_button);


            moreBtn.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(Activity_Offers.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_offer_options, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();

                    if (itemId == R.id.menu_view) {
                        Intent viewIntent = new Intent(Activity_Offers.this, Activity_Job_Details.class);
                        viewIntent.putExtra("offer_id", offerId);
                        startActivity(viewIntent);
                        return true;
                    } else if (itemId == R.id.menu_edit) {
                        Intent editIntent = new Intent(Activity_Offers.this, Activity_EditJobOffer.class);
                        editIntent.putExtra("offer_id", offerId);
                        startActivity(editIntent);
                        return true;
                    } else if (itemId == R.id.menu_delete) {
                        deleteOffer(offerId);
                        return true;
                    }

                    return false;
                });

                popupMenu.show();
            });

            acceptedView.setText(String.valueOf(accepted));
            pendingView.setText(String.valueOf(pending));
            rejectedView.setText(String.valueOf(rejected));

            // Add row to the table
            tableLayout.addView(row);
        }
    }

    private void deleteOffer(int offerId) {
        // Call your API to delete the offer and refresh the list if needed
        Log.d(TAG, "Delete offer ID: " + offerId);
        // You can add a confirmation dialog and API call here
    }


}
