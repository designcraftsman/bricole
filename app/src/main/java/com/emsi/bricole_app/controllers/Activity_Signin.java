package com.emsi.bricole_app.controllers;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.emsi.bricole_app.R;

public class Activity_Signin extends AppCompatActivity {

    private Button mBtnLogin;
    private TextView mBtnSignupRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // Set the correct layout
        mBtnSignupRedirect = findViewById(R.id.btnSignupRedirect);

        mBtnLogin = findViewById(R.id.btnLogin);

        mBtnLogin.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_Employer_Dashboard.class);
            startActivity(intent);
        });

        mBtnSignupRedirect.setOnClickListener(view->{
            Intent intent = new Intent(this , MainActivity.class);
            startActivity(intent);
        });
    }
}
