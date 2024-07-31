package com.example.womensafety;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText fullNameEditText, phoneNumberEditText, emailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        fullNameEditText = findViewById(R.id.signup_full_name);
        phoneNumberEditText = findViewById(R.id.signup_phone_number);
        emailEditText = findViewById(R.id.signup_email);
        Button signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(v -> handleSignup());
    }

    private void handleSignup() {
        String fullName = fullNameEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(email)) {
            Toast.makeText(SignupActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed to OTP validation page
        Intent intent = new Intent(SignupActivity.this, OtpValidationActivity.class);
        startActivity(intent);
    }
}
