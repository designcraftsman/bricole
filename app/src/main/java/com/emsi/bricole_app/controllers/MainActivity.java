package com.emsi.bricole_app.controllers;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.emsi.bricole_app.R;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mBtnSignup;
    private Button mBtnSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mBtnSignup = findViewById(R.id.btnSignup);

        mBtnSignin = findViewById(R.id.btnLoginRedirect);

        mBtnSignup.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_SignUp_UserType.class);
            startActivity(intent);
        });

        mBtnSignin.setOnClickListener(view->{
            Intent intent = new Intent(this , Activity_Signin.class);
            startActivity(intent);
        });
    }
}