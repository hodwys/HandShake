package com.example.handshake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MySavedDonationsActivity extends AppCompatActivity {

    RecyclerView recyclerView2;
    DatabaseReference database;
    adapterForMySavedDonations adapter;
    ArrayList<SavedDonation> savedDonationsList;

    Button goBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_saved_donations);

        recyclerView2 = findViewById(R.id.mySavedDonations);
        goBack = findViewById(R.id.backbtn2);
        database = FirebaseDatabase.getInstance().getReference("TakenDonations");
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        savedDonationsList = new ArrayList<>();
        adapter = new adapterForMySavedDonations(this, savedDonationsList);
        recyclerView2.setAdapter(adapter);

        // Access Firebase and get user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        String userID = user.getUid();


        adapter.setRepostOnClickListener(new adapterForMySavedDonations.OnRepostDonationClickListener() {
            @Override
            public void OnRepostDonationClick(SavedDonation donation) {
                repostDonationToFirebase(donation);
            }
        });


        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MySavedDonationsActivity.this, RecipientProfileActivity.class));
            }
        });

        adapter.setGotOnClickListener(new adapterForMySavedDonations.OnGotDonationClickListener() {
            @Override
            public void OnGotDonationClick(SavedDonation donation) {
                float rating = donation.getRating();
                sendRatingAfterGettingDonation(donation, rating);
            }
        });

        // Show all donations saved by the specific user
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedDonationsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    SavedDonation donation = dataSnapshot.getValue(SavedDonation.class);
                    if (donation.getRecipientID().equals(userID)){
                        savedDonationsList.add(donation);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void repostDonationToFirebase(SavedDonation donation) {
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("Donations");
        DatabaseReference takenDonationsRef = FirebaseDatabase.getInstance().getReference("TakenDonations");

        String originalDonationKey = donation.getDonationID();
        if (originalDonationKey == null) {
            return;
        }

        // Get the donation data from "TakenDonations"
        takenDonationsRef.child(originalDonationKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    // Get the donation data
                    postedDonation repostedDonation = dataSnapshot.getValue(postedDonation.class);

//                    repostedDonation.setRecipientName(null);
//                    repostedDonation.setRecipientID(null);
//                    repostedDonation.setRecipientInfo(null);

                    // Add the donation to "Donations" with a new key
                    String newTakenDonationKey = takenDonationsRef.push().getKey();
                    donationsRef.child(newTakenDonationKey).setValue(donation);


                    // Remove the donation from "TakenDonations"
                    takenDonationsRef.child(originalDonationKey).removeValue();

                    // Show a Toast indicating success
                    Toast.makeText(MySavedDonationsActivity.this, "Donation reposted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MySavedDonationsActivity.this, "Couldn't find data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(MySavedDonationsActivity.this, "Failed to repost donation. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Rate donation and remove donation from DB completely

    private void sendRatingAfterGettingDonation(SavedDonation donation, float rating) {
        String donorID = donation.getUserID();
        String donationID = donation.getDonationID();
        String donationName = donation.getDonationName();

        // Check for null values and handle gracefully
        if (donorID == null || donationID == null || donationName == null) {
            Toast.makeText(this, "Error: Missing information. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(donorID);
        DatabaseReference ratingRef = userRef.child("Rating").child(donationName);


        // Update rating with success/error handling
        ratingRef.setValue(rating)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MySavedDonationsActivity.this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                        // Optional actions here (disable button, update UI)
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MySavedDonationsActivity.this, "Failed to submit rating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



        DatabaseReference takenDonationsRef = FirebaseDatabase.getInstance().getReference("TakenDonations");
        takenDonationsRef.child(donationID).removeValue();
    }





}