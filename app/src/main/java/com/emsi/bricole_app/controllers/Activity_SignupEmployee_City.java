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

public class Activity_SignupEmployee_City extends AppCompatActivity {

    private TextView mBtnLoginRedirect;

    private AutoCompleteTextView mEmployeeCityOptions;

    private TextInputLayout mCityPlaceholder;
    private Button mCityNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_employee_city); // Set the correct layout

        mBtnLoginRedirect = findViewById(R.id.btnLoginRedirect);

        mEmployeeCityOptions = findViewById(R.id.employee_city_options);

        mCityNext = findViewById(R.id.city_next);

        mCityPlaceholder = findViewById(R.id.city_placeholder_text);

        mCityPlaceholder.setHint("Choissisez une ville");

        mCityNext.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_Signup_Form.class);
            startActivity(intent);
        });
        String[] cities = {"casablanca","Marrakech","Rabat"};

        ArrayAdapter<String> adapter =  new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,cities);

        mEmployeeCityOptions.setAdapter(adapter);

        mBtnLoginRedirect.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_Signin.class);
            startActivity(intent);
        });
    }
}
