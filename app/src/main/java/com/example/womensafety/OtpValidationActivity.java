package com.example.womensafety;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OtpValidationActivity extends AppCompatActivity {

    private EditText otpInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation);

        otpInput = findViewById(R.id.otp_input);
        Button validateOtpButton = findViewById(R.id.validate_otp_button);

        validateOtpButton.setOnClickListener(v -> {
            String otp = otpInput.getText().toString().trim();

            if (TextUtils.isEmpty(otp)) {
                Toast.makeText(OtpValidationActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                return;
            }

            // Here, add your OTP validation logic
            if (validateOtp(otp)) {
                Toast.makeText(OtpValidationActivity.this, "OTP Verified Successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(OtpValidationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(OtpValidationActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateOtp(String otp) {
        // Add your OTP validation logic here
        // For demonstration purposes, let's assume any 6-digit OTP is valid
        return otp.length() == 6;
    }
}
