package com.example.loginapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class activity_sign_up extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText signupEmail, signupPassword, signupName, signupMobile;
    private Button signupButton;
    private TextView loginRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupName = findViewById(R.id.signup_email1); // Add EditText for name
        signupMobile = findViewById(R.id.signup_email23); // Add EditText for mobile number
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                final String name = signupName.getText().toString().trim(); // Get name from EditText
                final String mobile = signupMobile.getText().toString().trim(); // Get mobile number from EditText

                if (email.isEmpty() || password.isEmpty() || name.isEmpty() || mobile.isEmpty()) {
                    Toast.makeText(activity_sign_up.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create user with email and password
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign up success, save user data to Realtime Database
                                    String userId = auth.getCurrentUser().getUid();

                                    // Create reference to "users" node in Realtime Database
                                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

                                    // Create a User object with name, email, mobile, etc.
                                    User user = new User(name, email, mobile);

                                    // Store user data under their UID in the database
                                    usersRef.child(userId).setValue(user);

                                    Toast.makeText(activity_sign_up.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(activity_sign_up.this, activity_login.class));
                                } else {
                                    // Sign up failed
                                    Toast.makeText(activity_sign_up.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(activity_sign_up.this, activity_login.class));
            }
        });
    }
}
