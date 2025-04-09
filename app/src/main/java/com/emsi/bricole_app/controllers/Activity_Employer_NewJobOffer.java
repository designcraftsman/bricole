package com.emsi.bricole_app.controllers;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.emsi.bricole_app.R;
import com.google.android.material.textfield.TextInputLayout;

public class Activity_Employer_NewJobOffer extends BaseDrawerActivity {

    private  TextView mBtnLoginRedirect;

    private TextInputLayout mCandidatesNumberPlaceholder;

    private AutoCompleteTextView mCandidatesNumberOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDrawer(R.layout.activity_employer_new_job_offer);

        mCandidatesNumberPlaceholder = findViewById(R.id.candidates_number);
        mCandidatesNumberOptions = findViewById(R.id.candidates_number_options);

        mCandidatesNumberPlaceholder.setHint("Votre choix");

        String[] candidates_number_options = {"1", "2", "3 ou plus"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, candidates_number_options);
        mCandidatesNumberOptions.setAdapter(adapter);

        // Force show dropdown when clicking
        mCandidatesNumberOptions.setOnClickListener(v -> mCandidatesNumberOptions.showDropDown());

        // Optional: Prevent keyboard from showing
        mCandidatesNumberOptions.setFocusable(false);
        mCandidatesNumberOptions.setInputType(0);
    }

}
