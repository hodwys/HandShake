package com.example.handshake; // Replace with your package name

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

public class DonorProfileActivity extends AppCompatActivity {

    TextView nameTextView, infoTextView, phoneTextView, rateTextView;
    Button postNewDonation, viewTakenDonation, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor_profile);

        // Get references to TextViews
        nameTextView = findViewById(R.id.nameProfile);
        infoTextView = findViewById(R.id.aboutMeProfile);
        phoneTextView = findViewById(R.id.phoneNumberProfile);
        rateTextView = findViewById(R.id.rateProfile);
        postNewDonation = findViewById(R.id.viewDonationsButton);
        viewTakenDonation = findViewById(R.id.goToDonorRequests);
        logout = findViewById(R.id.logoutdnr);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonorProfileActivity.this, LoginActivity.class));
            }
        });

        // post new donation -> go to relevant page
        postNewDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonorProfileActivity.this, PostNewDonation.class));
            }
        });


        // View taken donations-> go to relevant page
        viewTakenDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DonorProfileActivity.this, MyPostedDonationActivity.class));
            }
        });

        // Access Firebase and get user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        String userID = user.getUid();


        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String userName = snapshot.child("Username").getValue(String.class);
                    String userInfo = snapshot.child("Info").getValue(String.class);
                    String userNumber = snapshot.child("Phone number").getValue(String.class);

                    // Set retrieved data to TextViews
                    nameTextView.setText(userName);
                    infoTextView.setText(userInfo);
                    phoneTextView.setText(userNumber);

                    // Calculate average rating
                    DatabaseReference ratingReference = snapshot.child("Rating").getRef();
                    ratingReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            float totalRating = 0;
                            int numRatings = 0;
                            for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                                float rating = ratingSnapshot.getValue(Float.class);
                                totalRating += rating;
                                numRatings++;
                            }
                            if (numRatings > 0) {
                                float averageRating = totalRating / numRatings;
                                rateTextView.setText("My Rating: " + averageRating);
                                reference.child(userID).child("CurrentRating").setValue(averageRating+ " ");
                            } else {
                                rateTextView.setText("No ratings yet");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(DonorProfileActivity.this, "Error calculating average rating: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(DonorProfileActivity.this, "User data not found " + userID, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DonorProfileActivity.this, "Error retrieving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        // Retrieve user data from Firebase
//        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    String userName = snapshot.child("Username").getValue(String.class);
//                    String userInfo = snapshot.child("Info").getValue(String.class);
//                    String userNumber = snapshot.child("Phone number").getValue(String.class);
//
//                    // Set retrieved data to TextViews
//                    nameTextView.setText(userName);
//                    infoTextView.setText(userInfo);
//                    phoneTextView.setText(userNumber);
//                } else {
//                    Toast.makeText(DonorProfileActivity.this, "User data not found " + userID, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(DonorProfileActivity.this, "Error retrieving data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }
}
