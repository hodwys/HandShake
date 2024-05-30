package com.example.handshake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

// This class shows all donations that a specific user has posted
public class MyPostedDonationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference databaseReference, donationsDatabaseReference;
    adapterForMyPostedDonations adapter;
    ArrayList<postedDonation> postedDonationArrayList, nonTakenDonationArrayList;
    Button goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posted_donation);

        recyclerView = findViewById(R.id.posedDonationRecyclerView);
        goback = findViewById(R.id.backbtn4);

        databaseReference = FirebaseDatabase.getInstance().getReference("TakenDonations");
        donationsDatabaseReference = FirebaseDatabase.getInstance().getReference("Donations");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postedDonationArrayList = new ArrayList<>();

        adapter = new adapterForMyPostedDonations(this, postedDonationArrayList);
        recyclerView.setAdapter(adapter);

        // Access Firebase and get user ID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        String userID = user.getUid();

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyPostedDonationActivity.this, DonorProfileActivity.class));
            }
        });




        adapter.setRepostOnClickListener(new adapterForMyPostedDonations.OnDeleteDonationClickListener() {
            @Override
            public void onDeleteDonationClick(postedDonation donation) {
                //repostDonationToFirebase(donation);
                deleteDonation(donation);
            }
        });


         //Get all donations donor posted and were saved
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the list to avoid duplicate entries
                postedDonationArrayList.clear();

                // Get donations from "TakenDonations" database
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    postedDonation donation = dataSnapshot.getValue(postedDonation.class);
                    if (donation.getUserID().equals(userID)) {
                        postedDonationArrayList.add(donation);
                    }
                }

                // Get donations from "Donations" database
                donationsDatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            postedDonation donation = snapshot.getValue(postedDonation.class);
                            // Add donation to the list if it matches the user ID
                            if (donation.getUserID().equals(userID)) {
                                donation.setDonationID(snapshot.getKey());
                                postedDonationArrayList.add(donation);
                            }
                        }
                        // Notify the adapter of the data change
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                        Toast.makeText(MyPostedDonationActivity.this, "Failed to get donations from 'Donations' database.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });



    }


    private void deleteDonation(postedDonation donation) {
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("Donations");
        DatabaseReference takenDonationsRef = FirebaseDatabase.getInstance().getReference("TakenDonations");
        String originalDonationKey = donation.getDonationID();

        if (originalDonationKey == null ) {
            Toast.makeText(MyPostedDonationActivity.this, "Didn't find donation", Toast.LENGTH_SHORT).show();
            return;
        }

        // First, check if the donation is in "Donations" database
        donationsRef.child(originalDonationKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getRef().removeValue();
                    Toast.makeText(MyPostedDonationActivity.this, "Donation deleted from Donations database", Toast.LENGTH_SHORT).show();
                } else {
                    // If not found in "Donations" database, check "TakenDonations" database
                    takenDonationsRef.child(originalDonationKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                dataSnapshot.getRef().removeValue();
                                Toast.makeText(MyPostedDonationActivity.this, "Donation deleted from TakenDonations database", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MyPostedDonationActivity.this, "Donation not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(MyPostedDonationActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyPostedDonationActivity.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }




    // Move the donation back from "TakenDonations" to "Donations" db
    private void repostDonationToFirebase(postedDonation donation) {
        DatabaseReference donationsRef = FirebaseDatabase.getInstance().getReference("Donations");
        DatabaseReference takenDonationsRef = FirebaseDatabase.getInstance().getReference("TakenDonations");

        String originalDonationKey = donation.getDonationID();
        if (originalDonationKey == null) {
            Toast.makeText(MyPostedDonationActivity.this, "Donation not found", Toast.LENGTH_SHORT).show();
        }

        // Get the donation data from "Donations"
        takenDonationsRef.child(originalDonationKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the donation data
                    postedDonation repostedDonation = dataSnapshot.getValue(postedDonation.class);
                    // Add the donation to "Donations" with a new key
                    String newTakenDonationKey = takenDonationsRef.push().getKey();
                    donationsRef.child(newTakenDonationKey).setValue(donation);

                    // Remove the donation from "TakenDonations"
                    takenDonationsRef.child(originalDonationKey).removeValue();
                    // Show a Toast indicating success
                    Toast.makeText(MyPostedDonationActivity.this, "Donation Deleted successfully", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MyPostedDonationActivity.this, "Couldn't find data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
                Toast.makeText(MyPostedDonationActivity.this, "Failed to repost donation. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


}