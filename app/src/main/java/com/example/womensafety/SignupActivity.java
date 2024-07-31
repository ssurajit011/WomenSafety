package com.example.womensafety;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    private EditText fullNameEditText;
    private EditText phoneNumberEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText otpEditText;
    private Button otpButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String verificationId;
    private boolean otpSent = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        fullNameEditText = findViewById(R.id.signup_full_name);
        phoneNumberEditText = findViewById(R.id.signup_phone_number);
        emailEditText = findViewById(R.id.signup_email);
        passwordEditText = findViewById(R.id.signup_password);
        otpEditText = findViewById(R.id.otp_input);
        otpButton = findViewById(R.id.otp_button);

        phoneNumberEditText.setText("+91");

        otpButton.setOnClickListener(v -> {
            if (otpSent) {
                verifyCode(otpEditText.getText().toString().trim());
            } else {
                sendVerificationCode(phoneNumberEditText.getText().toString().trim());
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        otpEditText.setText(phoneAuthCredential.getSmsCode());
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(SignupActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("OTP", Objects.requireNonNull(e.getMessage()));
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificationId = s;
                        otpSent = true;
                        otpButton.setText( "Verify OTP");
                        Toast.makeText(SignupActivity.this, "Code sent", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyCode(String code) {
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Enter OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                saveUserToDatabase();
            } else {
                Toast.makeText(SignupActivity.this, "Verification Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToDatabase() {
        String fullName = fullNameEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("phoneNumber", phoneNumber);
        user.put("email", email);
        user.put("password", password);

        mDatabase.child("users").push().setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
