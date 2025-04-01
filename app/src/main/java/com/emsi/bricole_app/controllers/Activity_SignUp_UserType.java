package com.emsi.bricole_app.controllers;
import android.content.Intent;
import android.os.Bundle;
import com.emsi.bricole_app.R;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;


public class Activity_SignUp_UserType extends AppCompatActivity {
    private Button mBtnSignupEmployee;
    private Button mBtnPreferenceNext;
    private Button mBtnSignupEmployer;
    private TextView mBtnLoginRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_type); // Set the correct layout
        mBtnSignupEmployer = findViewById(R.id.btnSignupEmployer);

        mBtnLoginRedirect = findViewById(R.id.btnLoginRedirect);

        mBtnSignupEmployee = findViewById(R.id.btnSignupJobSeeker);

        mBtnLoginRedirect.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_Signin.class);
            startActivity(intent);
        });

        mBtnSignupEmployer.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_Signup_Form.class);
            startActivity(intent);
        });

        mBtnSignupEmployee.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_SignUpEmployee_Preference.class);
            startActivity(intent);
        });

    }
}
