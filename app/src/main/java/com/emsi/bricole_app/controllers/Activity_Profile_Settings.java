package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.emsi.bricole_app.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Activity_Profile_Settings extends Drawer {

    private ImageButton mBackBtn;
    private LinearLayout mPersonalInformation, mQualificationsSection, mPreferenceSection, mAvailabilitySection;

    private TextView mUserName, mEmail, mPhone, mLocation;
    private ImageView mProfileImage;
    private SharedPreferences prefs;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private final String PROFILE_DATA_URL = "http://10.0.2.2:8080/api/profile";
    private final String UPDATE_IMAGE_URL = "http://10.0.2.2:8080/api/profile/image";
    private String USER_ROLE;

    private final android.os.Handler autoUpdateHandler = new android.os.Handler();
    private final int REFRESH_INTERVAL_MS = 10000; // every 10 seconds

    private final Runnable autoUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            String token = prefs.getString("access_token", null);
            if (token != null) {
                fetchUserProfile(token);  // re-fetch profile
            }
            autoUpdateHandler.postDelayed(this, REFRESH_INTERVAL_MS); // schedule next run
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        autoUpdateHandler.post(autoUpdateRunnable);
    }

    @Override
    protected void onPause() {
        super.onPause();
        autoUpdateHandler.removeCallbacks(autoUpdateRunnable);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_profile_settings);

        prefs = getSharedPreferences("auth", MODE_PRIVATE);
        final String USER_ACCESS_TOKEN = prefs.getString("access_token", null);
        USER_ROLE = prefs.getString("role", null);

        // UI References
        mUserName = findViewById(R.id.userName);
        mEmail = findViewById(R.id.email);
        mPhone = findViewById(R.id.phone);
        mLocation = findViewById(R.id.location);
        mProfileImage = findViewById(R.id.profileImage);

        // Navigation Elements
        mPersonalInformation = findViewById(R.id.personal_information);
        mQualificationsSection = findViewById(R.id.qualificationsSection);
        mPreferenceSection = findViewById(R.id.preferencesSection);
        mAvailabilitySection = findViewById(R.id.availabilitySection);

        // ✅ Hide sections if user is an employer
        if ("EMPLOYER".equalsIgnoreCase(USER_ROLE)) {
            mQualificationsSection.setVisibility(LinearLayout.GONE);
            mPreferenceSection.setVisibility(LinearLayout.GONE);
            mAvailabilitySection.setVisibility(LinearLayout.GONE);
        }

        // Set listeners
        mBackBtn = findViewById(R.id.backButton);
        mProfileImage.setOnClickListener(v -> openImagePicker());
        mBackBtn.setOnClickListener(view -> finish());

        mPersonalInformation.setOnClickListener(view -> {
            startActivity(new Intent(this, Activity_Profile_Personal_Information.class));
        });
        mPreferenceSection.setOnClickListener(view -> {
            startActivity(new Intent(this, Activity_Profile_Preference.class));
        });
        mQualificationsSection.setOnClickListener(view -> {
            startActivity(new Intent(this, Activity_Profile_Skills.class));
        });
        mAvailabilitySection.setOnClickListener(view -> {
            startActivity(new Intent(this, Activity_Profile_Employee_Availability.class));
        });

        fetchUserProfile(USER_ACCESS_TOKEN);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            mProfileImage.setImageURI(selectedImageUri); // Preview it
            uploadImageToServer(selectedImageUri);
        }
    }

    private void uploadImageToServer(Uri imageUri) {
        try {
            InputStream iStream = getContentResolver().openInputStream(imageUri);
            byte[] inputData = getBytes(iStream);

            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, UPDATE_IMAGE_URL,
                    response -> {
                        // Handle response
                        Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show();
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }) {

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + prefs.getString("access_token", null));
                    return headers;
                }

                @Override
                public Map<String, DataPart> getByteData() {
                    Map<String, DataPart> params = new HashMap<>();

                    String mimeType = getContentResolver().getType(imageUri);
                    String extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

                    if (mimeType == null || extension == null) {
                        Toast.makeText(Activity_Profile_Settings.this, "Unsupported image type", Toast.LENGTH_SHORT).show();
                        return params;
                    }

                    extension = extension.toLowerCase();
                    mimeType = mimeType.toLowerCase();
                    System.out.println("the image mime type is: " + mimeType);
                    // Allow only png, webp, jpg, jpeg
                    if (!extension.matches("png|webp|jpg|jpeg")) {
                        Toast.makeText(Activity_Profile_Settings.this, "Only PNG, WEBP, JPG, or JPEG are supported", Toast.LENGTH_SHORT).show();
                        return params;
                    }

                    String fileName = "profile." + extension;
                    params.put("file", new DataPart(fileName, inputData, mimeType));

                    return params;
                }

            };

            Volley.newRequestQueue(this).add(multipartRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }



    private void fetchUserProfile(String token) {


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, PROFILE_DATA_URL, null,
                response -> {
                    try {
                        String email = response.getString("email");
                        String firstname = response.getString("firstname");
                        String lastname = response.getString("lastname");
                        int prefix = response.getInt("phoneNumberPrefix");
                        String phone = response.getString("phoneNumber");
                        String address = response.getString("address");
                        String profilePictureFilename = response.getString("profilePicture");
                        String imageUrl = "http://10.0.2.2:8080/images/profile/"
                                + URLEncoder.encode(profilePictureFilename, "UTF-8")
                                + "?t=" + System.currentTimeMillis(); // Bust cache

                        mUserName.setText(firstname + " " + lastname);
                        mEmail.setText(email);
                        mPhone.setText("+" + prefix + " " + phone);
                        mLocation.setText(address);

                        Glide.with(this)
                                .load(imageUrl)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(mProfileImage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    error.printStackTrace(); // You can log or show error UI
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
}
