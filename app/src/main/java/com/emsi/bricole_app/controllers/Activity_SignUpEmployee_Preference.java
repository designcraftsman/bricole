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


public class Activity_SignUpEmployee_Preference extends AppCompatActivity {

    private AutoCompleteTextView mSectorsOptions;
    private TextView mBtnLoginRedirect;
    private TextInputLayout mPreferencePlaceholder;
    private Button mPreferenceNext;

    private Button mPreferencePrevious;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_employer_preference); // Set the correct layout

        mBtnLoginRedirect = findViewById(R.id.btnLoginRedirect);

        mPreferencePlaceholder = findViewById(R.id.preferencePlaceholder);

        mPreferencePlaceholder.setHint("Choisissez votre spécialisation");

        mPreferencePrevious = findViewById(R.id.preference_previous);

        mPreferencePrevious.setOnClickListener(view->{
            finish();
        });

        mSectorsOptions = findViewById(R.id.sectors_options);

        String[] sectors = {"Eléctricien","Plombier","Ménage","Technicien","Mécanicien"};

        ArrayAdapter<String> adapter =  new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,sectors);

        mPreferenceNext = findViewById(R.id.preference_next);

        mPreferenceNext.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_SignupEmployee_City.class);
            startActivity(intent);
        });
        mSectorsOptions.setAdapter(adapter);

        mBtnLoginRedirect.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_Signin.class);
            startActivity(intent);
        });
    }
}
