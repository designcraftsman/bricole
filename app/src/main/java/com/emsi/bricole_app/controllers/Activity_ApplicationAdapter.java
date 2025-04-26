package com.emsi.bricole_app.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Application;

import java.util.List;

public class Activity_ApplicationAdapter extends RecyclerView.Adapter<Activity_ApplicationAdapter.ViewHolder> {

    private List<Application> applicationList;
    private OnCancelClickListener onCancelClickListener;

    public interface OnCancelClickListener {
        void onCancelClick(int applicationId, int position);
    }

    public Activity_ApplicationAdapter(List<Application> applicationList, OnCancelClickListener listener) {
        this.applicationList = applicationList;
        this.onCancelClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_application_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application application = applicationList.get(position);

        holder.postedBy.setText("Offre reçue");
        holder.jobTitle.setText(application.getTitle());
        holder.jobLocation.setText(application.getLocation());
        holder.postTime.setText(formatDate(application.getCreatedAt()));
        holder.badgeNew.setVisibility(application.getState().equals("PENDING") ? View.VISIBLE : View.GONE);

        holder.cancelButton.setOnClickListener(v -> {
            if (onCancelClickListener != null) {
                onCancelClickListener.onCancelClick(application.getId(), holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public void removeItem(int position) {
        applicationList.remove(position);
        notifyItemRemoved(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView postedBy, jobTitle, jobLocation, badgeNew, postTime;
        Button cancelButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            postedBy = itemView.findViewById(R.id.postedBy);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            badgeNew = itemView.findViewById(R.id.badgeNew);
            postTime = itemView.findViewById(R.id.postTime);
            cancelButton = itemView.findViewById(R.id.cancel_button);
        }
    }

    private String formatDate(String isoDate) {
        return "Publié " + isoDate.substring(0, 10);
    }
}
