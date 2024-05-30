package com.example.handshake;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {
    EditText username, phone, aboutUser;
    String getUsername, getPhone, getAboutUser, getChoice;
    RadioGroup radioGroup;
    Button confirmChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        username = findViewById(R.id.editUsername);
        phone = findViewById(R.id.editPhone);
        aboutUser = findViewById(R.id.aboutUserEdit);
        radioGroup = findViewById(R.id.radioGroup);
        confirmChanges = findViewById(R.id.makeChanges);



        confirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsername = username.getText().toString();
                getPhone = phone.getText().toString();
                getAboutUser = aboutUser.getText().toString();

                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                getChoice = selectedRadioButton.getText().toString();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                String userID = currentUser.getUid();

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userID);

//                Map<String, Object> updatedDetails = new HashMap<>();
//                if (!getUsername.equals(getUsernameFromDatabase)) {
//                    updatedDetails.put("username", getUsername);
//                }
//                if (!getPhone.equals(getPhoneFromDatabase)) {
//                    updatedDetails.put("phone", getPhone);
//                }
//                if (!getAboutUser.equals(getAboutUserFromDatabase)) {
//                    updatedDetails.put("about", getAboutUser);
//                }
//                if (!getChoice.equals(getChoiceFromDatabase)) {
//                    updatedDetails.put("choice", getChoice);
//                }



            }
        });


    }
}