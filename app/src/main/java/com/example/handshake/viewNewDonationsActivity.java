package com.example.handshake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class viewNewDonationsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    adapterForDonationSearch myAdapter;
    ArrayList<Donation> dList;

    Spinner filterLocation, filterType;
    Button filterDonations, backbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_new_donations);

        recyclerView = findViewById(R.id.donationList);
        database = FirebaseDatabase.getInstance().getReference("Donations");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dList = new ArrayList<>();
        myAdapter = new adapterForDonationSearch(this, dList);
        recyclerView.setAdapter(myAdapter);

        filterLocation = findViewById(R.id.donationLocationFilter);
        filterType = findViewById(R.id.donationTypeFiler);
        filterDonations = findViewById(R.id.filterDonations);
        backbtn = findViewById(R.id.backbtn);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(viewNewDonationsActivity.this, RecipientProfileActivity.class));
            }
        });


        // Saving Donations when clicking the button
        myAdapter.setOnItemClickListener(new adapterForDonationSearch.OnItemClickListener() {

            @Override
            public void onSaveDonationClick(Donation donation) {
                // Handle Save Donation click event
                saveDonationAndRemoveFromOriginalList(donation);
            }
        });


        // DROP DOWN MENU FOR TYPE
        ArrayAdapter<CharSequence> adaptertype = ArrayAdapter.createFromResource(
                this,
                R.array.type,
                android.R.layout.simple_spinner_item
        );
        // Set a drop down menu with all donation types
        adaptertype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterType.setAdapter(adaptertype);

        // Selecting a type for donation filter
        filterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedType = parentView.getItemAtPosition(position).toString();
                //Toast.makeText(viewNewDonationsActivity.this, "Selected: " + selectedType, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // DROP DOWN MENU FOR LOCATION
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.regions,
                android.R.layout.simple_spinner_item
        );
        // Set a drop down menu with all regions in Israel
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterLocation.setAdapter(adapter);

        // Selecting a region for donation post
        filterLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedRegion = parentView.getItemAtPosition(position).toString();
                //Toast.makeText(viewNewDonationsActivity.this, "Selected: " + selectedRegion, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        filterDonations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the method to filter donations
                filterDonations();
            }
        });

    }


    // Method to filter donations based on selected location and type
    private void filterDonations() {
        // Access User database and get userID
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        String userID = user.getUid();

        // Get selected location and type
        String selectedLocation = filterLocation.getSelectedItem().toString();
        String selectedType = filterType.getSelectedItem().toString();

        // Filter donations based on location and type
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    dList.clear(); // Clear the existing data before adding new data

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String name = dataSnapshot.child("donationName").getValue(String.class);
                        String info = dataSnapshot.child("donationInfo").getValue(String.class);
                        String uid = dataSnapshot.child("userID").getValue(String.class);
                        String location = dataSnapshot.child("donationLocation").getValue().toString();
                        String type = dataSnapshot.child("donationType").getValue().toString();

                        if (selectedLocation.equals(location) && selectedType.equals(type)) {
                            // Create a new object with only the necessary fields
                            Donation donation = new Donation();
                            donation.setName(name);
                            donation.setInfo(info);
                            donation.setUserID(uid);
                            donation.setDonationLocation(location);
                            donation.setDonationType(type);

                            // Store the donation key
                            donation.setDonationID(dataSnapshot.getKey());

                            // Retrieve "Username" from "User" database based on matching user ID
                            reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                    if (userSnapshot.exists()) {
                                        String username = userSnapshot.child("Username").getValue(String.class);
                                        String donorInfo = userSnapshot.child("Info").getValue(String.class);
                                        String donorPhone = userSnapshot.child("Phone number").getValue(String.class);
                                        String donorRate = userSnapshot.child("CurrentRating").getValue(String.class);


                                        donation.setUsername(username);
                                        donation.setDonorRate(donorRate);
                                        donation.setDonorInfo(donorInfo);
                                        donation.setDonorPhone(donorPhone);

                                        dList.add(donation);
                                        myAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed

                                    } else {
                                        Toast.makeText(viewNewDonationsActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle errors if needed
                                }
                            });
                        }
                        else if (dList.isEmpty()){
                            //Toast.makeText(viewNewDonationsActivity.this, "No current kind of donations in this area! Can you travel anywhere else? ", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(viewNewDonationsActivity.this, "Donation data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }

    private void saveDonationAndRemoveFromOriginalList(Donation donation) {
        DatabaseReference takenDonationsRef = FirebaseDatabase.getInstance().getReference("TakenDonations");
        DatabaseReference originalDonationsRef = FirebaseDatabase.getInstance().getReference("Donations");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        String recipientID = user.getUid();
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User").child(recipientID);

        String takenDonationKey = donation.getDonationID();

        if (takenDonationKey == null) {
            return;
        }

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                if (userSnapshot.exists()) {
                    String recipientName = userSnapshot.child("Username").getValue(String.class);
                    String recipientPhone = userSnapshot.child("Phone number").getValue(String.class);
                    String recipientInfo = userSnapshot.child("Info").getValue(String.class);

                    donation.setRecipientName(recipientName);
                    donation.setRecipientInfo(recipientInfo);
                    donation.setRecipientPhone(recipientPhone);
                    donation.setRecipientID(recipientID);

                    String category = donation.getDonationType();
                    String currentDate = getCurrentDate();
                    DatabaseReference categoryRef = userReference.child("savedDonations").child(category);

                    categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean donationSavedToday = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String savedDate = snapshot.getKey();
                                if (savedDate.equals(currentDate)) {
                                    donationSavedToday = true;
                                    break;
                                }
                            }

                            if (!donationSavedToday) {
                                takenDonationsRef.child(takenDonationKey).setValue(donation)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(viewNewDonationsActivity.this, "Donation saved successfully", Toast.LENGTH_SHORT).show();

                                                categoryRef.child(currentDate).setValue(true);
                                                originalDonationsRef.child(takenDonationKey).removeValue();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(viewNewDonationsActivity.this, "Failed to save donation", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(viewNewDonationsActivity.this, "You already saved a donation from this category today", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle errors if needed
                        }
                    });
                } else {
                    Toast.makeText(viewNewDonationsActivity.this, "Recipient data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors if needed
            }
        });
    }


    // Method to get the current date in a suitable format for saving in Firebase
    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }




}