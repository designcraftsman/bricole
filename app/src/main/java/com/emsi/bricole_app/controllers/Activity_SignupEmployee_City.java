package com.emsi.bricole_app.controllers;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.emsi.bricole_app.R;

public class Activity_SignupEmployee_City extends AppCompatActivity {

    private TextView mBtnLoginRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_employer_city); // Set the correct layout

        mBtnLoginRedirect = findViewById(R.id.btnLoginRedirect);

        mBtnLoginRedirect.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_Signin.class);
            startActivity(intent);
        });
    }
}
