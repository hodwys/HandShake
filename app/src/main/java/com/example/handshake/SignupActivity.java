package com.example.handshake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    EditText username;
    EditText email;
    EditText phone;
    EditText password;
    EditText passwordConfirm;
    RadioGroup radioGroup;
    EditText aboutUser;

    Button signupBtn;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String userID;

    // Fields to store user data
    String getUsername;
    String getEmail;
    String getPhone;
    String getPassword;
    String getInfo;
    String getChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Find all ids in xml
        username = findViewById(R.id.signupUsername);
        email = findViewById(R.id.signupEmail);
        phone = findViewById(R.id.signupPhone);
        password = findViewById(R.id.signupPassword);
        passwordConfirm = findViewById(R.id.signupPasswordConfirm);
        radioGroup = findViewById(R.id.radioGroup);
        aboutUser = findViewById(R.id.aboutUserSignup);

        auth = FirebaseAuth.getInstance();
        signupBtn = findViewById(R.id.buttonSignup);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        // When clicking the signup button, the program authenticate the data (makes sure it is all filled it and correctly, as well as makes sure the email wasn't already used).
        // Another important thing this function does is adding our data into real time Firebase (with all its information)
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsername = username.getText().toString();
                getEmail = email.getText().toString();
                getPhone = phone.getText().toString();
                getPassword = password.getText().toString();
                getInfo = aboutUser.getText().toString();

                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                getChoice = selectedRadioButton.getText().toString();

                if (TextUtils.isEmpty(getEmail) || TextUtils.isEmpty(getPassword) || TextUtils.isEmpty(getUsername) || TextUtils.isEmpty(getInfo) || TextUtils.isEmpty(getPhone) || selectedRadioButtonId == -1) {
                    Toast.makeText(SignupActivity.this, "Please fill in all fields to signup.", Toast.LENGTH_SHORT).show();
                } else if (getPassword.length() < 8) {
                    Toast.makeText(SignupActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                } else if (!getPassword.equals(passwordConfirm.getText().toString())) {
                    Toast.makeText(SignupActivity.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
                } else if (!isValidEmail(getEmail)) {
                    Toast.makeText(SignupActivity.this, "Email format isn't correct, please enter a valid email", Toast.LENGTH_SHORT).show();
                } else {
                    // Register user using Firebase Authentication
                    registerUser(getEmail, getPassword);
                }
            }
        });

        // Set the selection between wanting to donate and receive a donation
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = findViewById(checkedId);
                Toast.makeText(getApplicationContext(), "Selected" + rb.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        // Directs the user to login page if has a user already
        TextView btn = findViewById(R.id.goToLoginPage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        aboutUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 50) {
                    // Show a toast if the text exceeds 50 characters
                    Toast.makeText(SignupActivity.this, "User info cannot exceed 50 characters", Toast.LENGTH_SHORT).show();


                    // Trim the text to 50 characters
                    aboutUser.setText(s.subSequence(0, 50));
                    aboutUser.setSelection(aboutUser.getText().length()); // Move cursor to end
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });


        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 16) {
                    // Show a toast if the text exceeds 16 characters
                    Toast.makeText(SignupActivity.this, "Username cannot exceed 16 characters", Toast.LENGTH_SHORT).show();

                    // Trim the text to 16 characters
                    username.setText(s.subSequence(0, 16));
                    username.setSelection(username.getText().length()); // Move cursor to end
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used
            }
        });
    }

    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignupActivity.this, "Signup was successful", Toast.LENGTH_SHORT).show();
                    // we ,ust first save the userID provided by the authentication
                    userID = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                    // so we can name the nodes of each user as its ID so we can later access it!
                    saveUserDataToDatabase();
                    redirectToLoginActivity();

                }
                else {
                    Toast.makeText(SignupActivity.this, "This email already exists. Please login.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // After authentication we must save the details into RealTime firebase
    private void saveUserDataToDatabase() {
        // Save information to Firebase Database
        HashMap<String, Object> usersMap = new HashMap<>();
        usersMap.put("Username", getUsername);
        usersMap.put("Email", getEmail);
        usersMap.put("Phone number", getPhone);
        usersMap.put("Password", getPassword);
        usersMap.put("Info", getInfo);
        usersMap.put("Choice", getChoice);

        databaseReference.child("User").child(userID).setValue(usersMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(SignupActivity.this, "Data added", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, "Couldn't add data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Function to validate email format using regex
    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }


    // Redirect to LoginActivity
    private void redirectToLoginActivity() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Optional: Finish the SignupActivity so the user can't navigate back to it
    }
}
