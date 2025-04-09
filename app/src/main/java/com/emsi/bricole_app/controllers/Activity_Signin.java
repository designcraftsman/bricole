package com.emsi.bricole_app.controllers;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.emsi.bricole_app.R;

public class Activity_Signin extends AppCompatActivity {

    private Button mBtnLogin;
    private TextView mBtnSignupRedirect;

    private EditText mEmailInput;

    private EditText mPasswordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin); // Set the correct layout
        mBtnSignupRedirect = findViewById(R.id.btnSignupRedirect);

        mEmailInput = findViewById(R.id.email_input);

        mPasswordInput = findViewById(R.id.password_input);

        mBtnLogin = findViewById(R.id.btnLogin);

        mBtnLogin.setOnClickListener(view->{
            String email = mEmailInput.getText().toString().trim();
            String password = mPasswordInput.getText().toString().trim();

            if (email.isEmpty()) {
                mEmailInput.setError("Email is required");
                mEmailInput.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                mPasswordInput.setError("Password is required");
                mPasswordInput.requestFocus();
                return;
            }

            if(email.equals("employer") && password.equals("employer")){
                Intent intent = new Intent(this , Activity_Employer_Dashboard.class);
                startActivity(intent);
            }else if(email.equals("employee") && password.equals(("employee"))){
                Intent intent = new Intent(this , Activity_Job_Listing.class);
                startActivity(intent);
            }

        });

        mBtnSignupRedirect.setOnClickListener(view->{
            Intent intent = new Intent(this , MainActivity.class);
            startActivity(intent);
        });
    }
}
