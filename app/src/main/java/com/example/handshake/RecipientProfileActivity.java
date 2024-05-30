package com.example.handshake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecipientProfileActivity extends AppCompatActivity {

    TextView name, phone, info;
    Button gotoDonationsFilter, goToMySavedDonations, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipient_profile);

        name = findViewById(R.id.nameReceiver);
        phone = findViewById(R.id.phoneReceiver);
        info = findViewById(R.id.infoReceiver);
        gotoDonationsFilter = findViewById(R.id.viewAllDonations);
        goToMySavedDonations = findViewById(R.id.goToMySavedDonations);
        logout = findViewById(R.id.logoutrcpt);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecipientProfileActivity.this, LoginActivity.class));
            }
        });


        // Go to donation Filters
        gotoDonationsFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecipientProfileActivity.this, viewNewDonationsActivity.class));
            }
        });

        goToMySavedDonations.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecipientProfileActivity.this, MySavedDonationsActivity.class));
            }
        });



        // Access Firebase and get user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        String userID = user.getUid();

        // Retrieve user data from Firebase
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("Username").getValue(String.class);
                    String userInfo = snapshot.child("Info").getValue(String.class);
                    String userNumber = snapshot.child("Phone number").getValue(String.class);

                    // Set retrieved data to TextViews
                    name.setText(userName);
                    info.setText(userInfo);
                    phone.setText(userNumber);
                } else {
                    Toast.makeText(RecipientProfileActivity.this, "User data not found " + userID, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipientProfileActivity.this, "Error retrieving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}