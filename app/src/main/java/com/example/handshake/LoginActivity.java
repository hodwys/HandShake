package com.example.handshake;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText password;
    EditText email;
    Button login;
    FirebaseAuth auth;
    String choice; // Whether we need to direct the user to donation or receiver

    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//
//        // Declare ids in xml
//        email = findViewById(R.id.LoginEmail);
//        password = findViewById(R.id.loginPassword);
//        login = findViewById(R.id.LoginButton);
//        auth = FirebaseAuth.getInstance();
//
//
//        // Access Firebase and get user ID
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null){
//            Toast.makeText(LoginActivity.this, "No user", Toast.LENGTH_SHORT).show();
//        }
//        else{
//            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
//            String userID = user.getUid();
//        }
//
//
//        // Login the user after making sure the details match.
//        login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String txt_email = email.getText().toString();
//                String txt_password = password.getText().toString();
//
//                // Check if email or password is empty
//                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
//                    // Show a Toast message
//                    Toast.makeText(LoginActivity.this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
//                    return; // Exit the onClick method early
//                }
//
//                // Continue with login process
//                loginUser(txt_email, txt_password);
//            }
//        });
//
//
//        // When clicking the don't have an account yet, we should switch to the create account activity.
//        TextView btn = findViewById(R.id.goToSignupPage);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
//            }
//        });
//    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Declare ids in xml
        email = findViewById(R.id.LoginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.LoginButton);
        auth = FirebaseAuth.getInstance();

        // Login the user after making sure the details match.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                // Check if email or password is empty
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    // Show a Toast message
                    Toast.makeText(LoginActivity.this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show();
                    return; // Exit the onClick method early
                }

                // Continue with login process
                loginUser(txt_email, txt_password);
            }
        });

        // When clicking the don't have an account yet, we should switch to the create account activity.
        TextView btn = findViewById(R.id.goToSignupPage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }


    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

            // Get the user's "Choice" from the Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(auth.getCurrentUser().getUid());
            userRef.get().addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    choice = dataSnapshot.child("Choice").getValue(String.class);

                    // Redirect based on the user's "Choice"
                    if ("אשמח לתרומה".equals(choice)) {
                        startActivity(new Intent(LoginActivity.this, RecipientProfileActivity.class));
                    } else if ("מעוניין לתרום".equals(choice)){
                        startActivity(new Intent(LoginActivity.this, DonorProfileActivity.class));
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Had an issue directing you to your profile.", Toast.LENGTH_SHORT).show();
                    }

                    finish();
                }
            }).addOnFailureListener(e -> {
                // Handle failure to get user data
                Toast.makeText(LoginActivity.this, "Failed to get user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
                })
                .addOnFailureListener(e -> {
                    // Handle login failure
                    Toast.makeText(LoginActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }





}