package com.example.blogapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class AccountActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    private TextView fullnameText;
    private TextView emailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        fullnameText = (TextView) findViewById(R.id.full_name_text_account);
        emailText = (TextView) findViewById(R.id.email_text_account);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.blogapp", Context.MODE_PRIVATE);

        fullnameText.setText(sharedPreferences.getString("fullname_user", "Not found"));

        emailText.setText(sharedPreferences.getString("email_user", "Not found"));

    }
}
