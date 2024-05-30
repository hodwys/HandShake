package com.example.handshake;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class adapterForDonationSearch extends RecyclerView.Adapter<adapterForDonationSearch.MyViewHolder> {

    Context context;
    ArrayList<Donation> list;
    ArrayList<Donation> filteredDonationsList;
    OnItemClickListener onItemClickListener;

    public void setDonationList(ArrayList<Donation> filteredDonations) {
        this.list = filteredDonations;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onSaveDonationClick(Donation donation);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public adapterForDonationSearch(Context context, ArrayList<Donation> list) {
        this.context = context;
        this.list = list;
        this.filteredDonationsList = new ArrayList<>(list);
    }

    public void setFilteredDonationsList(ArrayList<Donation> filteredDonationsList) {
        this.filteredDonationsList = filteredDonationsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Donation donation = list.get(position);
        holder.donationName.setText(donation.getName());
        holder.donationInfo.setText(donation.getInfo());
        holder.donorName.setText(donation.getDonorName());
        holder.donorInfo.setText(donation.getDonorInfo());
        holder.donorPhone.setText(donation.getDonorPhone());
        holder.donorRate.setText(donation.getDonorRate());

        // Set a click listener for the "Save Donation" button
        holder.reserveDonationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    // Call the interface method to handle the Save Donation click
                    onItemClickListener.onSaveDonationClick(donation);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView donationName, donationInfo, donorName, donorPhone, donorInfo, donorRate;
        Button reserveDonationBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            donationName = itemView.findViewById(R.id.showDonationName);
            donationInfo = itemView.findViewById(R.id.showDonationInfo);
            donorName = itemView.findViewById(R.id.showDonorName);
            donorPhone = itemView.findViewById(R.id.showDonorPhone);
            donorInfo = itemView.findViewById(R.id.showDonorInfo);
            donorRate = itemView.findViewById(R.id.showDonorRate);
            reserveDonationBtn = itemView.findViewById(R.id.saveDonation);


        }
    }
}