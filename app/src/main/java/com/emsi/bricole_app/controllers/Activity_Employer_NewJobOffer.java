package com.emsi.bricole_app.controllers;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.emsi.bricole_app.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_Employer_NewJobOffer extends Drawer {

    private TextView mBtnLoginRedirect;
    private EditText mJobTitle;
    private EditText mJobDescription;
    private EditText mJobSalary;
    private EditText mJobLocation;
    private AutoCompleteTextView mJobCategoryOptions;
    private LinearLayout missionsContainer;
    private LinearLayout mediaContainer;
    private SharedPreferences prefs;
    private Button addMission;
    private Button addMedia;
    private List<Uri> selectedImages = new ArrayList<>();

    final String API_URL = "http://10.0.2.2:8080/api/employer/job/create";
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_new_job_offer);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        final String USER_ACCESS_TOKEN = prefs.getString("access_token", null);

        // Initialize views
        mJobTitle = findViewById(R.id.job_offer_title);
        mJobSalary = findViewById(R.id.job_offer_salary);
        mJobDescription = findViewById(R.id.job_offer_description);
        mJobCategoryOptions = findViewById(R.id.job_offer_category_options);
        mJobLocation = findViewById(R.id.job_offer_location);
        missionsContainer = findViewById(R.id.missions_container);
        mediaContainer = findViewById(R.id.media_container);

        addMission = findViewById(R.id.add_mission_button);
        addMedia = findViewById(R.id.add_media_button);
        addMission.setOnClickListener(v -> addMissionField());
        addMedia.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST);
        });

        Button publishBtn = findViewById(R.id.btn_new_job);
        publishBtn.setOnClickListener(view -> submitJobOffer(USER_ACCESS_TOKEN));

        // Category dropdown setup
        String[] category_options = {"PLUMBING", "ELECTRICAL", "CARPENTRY", "PAINTING", "GARDENING", "CLEANING", "MOVING", "OTHER"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, category_options);
        mJobCategoryOptions.setAdapter(adapter);
        mJobCategoryOptions.setOnClickListener(v -> mJobCategoryOptions.showDropDown());
        mJobCategoryOptions.setFocusable(false);
        mJobCategoryOptions.setInputType(0);
    }

    private void addMissionField() {
        EditText newMission = new EditText(this);
        newMission.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        newMission.setHint("Décrire la mission");
        newMission.setBackground(getDrawable(R.drawable.component_input));
        newMission.setPadding(20, 20, 20, 20);
        newMission.setTextColor(Color.BLACK);
        newMission.setTextSize(14f);
        newMission.setGravity(Gravity.START | Gravity.TOP);

        missionsContainer.addView(newMission);
        animateViewIn(newMission);  // 🟠 Animate here
    }

    private void animateViewIn(View view) {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.startAnimation(animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // Handle multiple images (append to existing)
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    if (!selectedImages.contains(imageUri)) {
                        selectedImages.add(imageUri);
                        addFileNameToMediaContainer(imageUri);
                    }
                }
            } else if (data.getData() != null) {
                // Handle single image (append to existing)
                Uri imageUri = data.getData();
                if (!selectedImages.contains(imageUri)) {
                    selectedImages.add(imageUri);
                    addFileNameToMediaContainer(imageUri);
                }
            }
        }
    }


    private void addFileNameToMediaContainer(Uri uri) {
        String fileName = "Unknown";

        // Try to extract the actual file name using content resolver
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex != -1) {
                fileName = cursor.getString(nameIndex);
            }
            cursor.close();
        }

        TextView fileNameView = new TextView(this);
        fileNameView.setText(fileName);
        fileNameView.setTextColor(getResources().getColor(android.R.color.black));
        fileNameView.setTextSize(14);
        fileNameView.setPadding(0, 8, 0, 8);

        mediaContainer.addView(fileNameView);
    }




    private void submitJobOffer(String token) {
        // Build the job JSON string
        JSONObject jobJson = new JSONObject();
        try {
            jobJson.put("title", mJobTitle.getText().toString().trim());
            jobJson.put("description", mJobDescription.getText().toString().trim());
            jobJson.put("location", mJobLocation.getText().toString().trim());
            jobJson.put("salary", Double.parseDouble(mJobSalary.getText().toString().trim()));
            jobJson.put("category", mJobCategoryOptions.getText().toString().trim());

            JSONArray missions = new JSONArray();
            for (int i = 0; i < missionsContainer.getChildCount(); i++) {
                EditText missionInput = (EditText) missionsContainer.getChildAt(i);
                String missionText = missionInput.getText().toString().trim();
                if (!missionText.isEmpty()) {
                    missions.put(missionText);
                }
            }
            jobJson.put("missions", missions);

            // Create and send the multipart request
            createMultipartRequest(
                    token,
                    jobJson,
                    "Job created successfully!",
                    "Error creating job: "
            );

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating job data", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Please enter a valid salary", Toast.LENGTH_SHORT).show();
        }
    }

    private void createMultipartRequest(String token, JSONObject jobJson,
                                        String successMessage, String errorMessage) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(
                Request.Method.POST,
                API_URL,
                response -> Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        ) {
            @Override
            public List<Map.Entry<String, VolleyMultipartRequest.DataPart>> getByteData() throws AuthFailureError {
                List<Map.Entry<String, VolleyMultipartRequest.DataPart>> data = new ArrayList<>();

                int index = 0;
                for (Uri uri : selectedImages) {
                    try {
                        InputStream iStream = getContentResolver().openInputStream(uri);
                        byte[] imgData = new byte[iStream.available()];
                        iStream.read(imgData);
                        iStream.close();

                        // Always use key "media"
                        data.add(new AbstractMap.SimpleEntry<>("media",
                                new VolleyMultipartRequest.DataPart("image" + index + ".jpg", imgData, "image/jpeg")));
                        index++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // Add JSON part (optional)
                data.add(new AbstractMap.SimpleEntry<>("job",
                        new VolleyMultipartRequest.DataPart("job.json", jobJson.toString().getBytes(), "application/json")));

                return data;
            }


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(multipartRequest);
    }
}
