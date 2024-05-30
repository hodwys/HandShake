package com.example.handshake;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class adapterForMySavedDonations extends RecyclerView.Adapter<adapterForMySavedDonations.MyViewHolder2> {

    Context context2;
    ArrayList<SavedDonation> savedDonationsList;
    OnGotDonationClickListener gotOnClickListener;
    OnRepostDonationClickListener repostOnClickListener;


    public interface OnRepostDonationClickListener {
        void OnRepostDonationClick(SavedDonation donation);
    }


    public void setRepostOnClickListener(OnRepostDonationClickListener listener) {
        this.repostOnClickListener = listener;
    }


    public adapterForMySavedDonations(Context context2, ArrayList<SavedDonation> savedDonationsList) {
        this.context2 = context2;
        this.savedDonationsList = savedDonationsList;
    }

    // Interface for Handling Clicks
    public interface OnGotDonationClickListener {
        void OnGotDonationClick(SavedDonation donation) ;
    }

    public void setGotOnClickListener(adapterForMySavedDonations.OnGotDonationClickListener listener) {
        this.gotOnClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context2).inflate(R.layout.mysaveddonations, parent,false);
        return new MyViewHolder2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, int position) {
        SavedDonation donation = savedDonationsList.get(position);
        holder.donationName.setText(donation.getDonationName());
        holder.donationInfo.setText(donation.getDonationInfo());
        holder.donationLocation.setText(donation.getDonationLocation());
        holder.donorName.setText(donation.getDonorName());
        holder.donorPhone.setText(donation.getDonorPhone());

        holder.gotDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gotOnClickListener != null) {
                    float rating = holder.ratingBar.getRating();
                    donation.setRating(rating);
                    gotOnClickListener.OnGotDonationClick(donation);
                }
            }
        });

        holder.repostDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gotOnClickListener != null) {
                    repostOnClickListener.OnRepostDonationClick(donation);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return savedDonationsList.size();
    }

    public static class MyViewHolder2 extends RecyclerView.ViewHolder{

        TextView donationName, donationInfo, donationLocation, donorName, donorPhone;
        Button gotDonation, repostDonation;
        RatingBar ratingBar;

        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);

            donationName = itemView.findViewById(R.id.savedDonationName);
            donationInfo = itemView.findViewById(R.id.savedDonationInfo);
            donationLocation = itemView.findViewById(R.id.savedDonationLocation);
            donorName = itemView.findViewById(R.id.savedDonorName);
            donorPhone = itemView.findViewById(R.id.savedDonorPhone);
            ratingBar = itemView.findViewById(R.id.donationRating);
            gotDonation = itemView.findViewById(R.id.gotDonation);
            repostDonation = itemView.findViewById(R.id.repostDonation);

        }
    }
}