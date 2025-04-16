package com.emsi.bricole_app.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.emsi.bricole_app.R;
import java.util.List;
import com.emsi.bricole_app.models.Job;

public class Activity_JobAdapter extends RecyclerView.Adapter<Activity_JobAdapter.JobViewHolder> {

    private List<Job> jobList;

    public Activity_JobAdapter(List<Job> jobList) {
        this.jobList = jobList;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_job_card, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.postedBy.setText("Par " + job.getPostedBy());
        holder.jobTitle.setText(job.getTitle());
        holder.jobLocation.setText(job.getLocation());
        holder.postTime.setText(job.getTimePosted());
        holder.badgeNew.setVisibility(job.isNew() ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(v -> {
            // Navigate to the job detail activity
            android.content.Context context = v.getContext();
            android.content.Intent intent = new android.content.Intent(context, Activity_Single_Job_Offer.class);
            intent.putExtra("job_id", job.getId()); // Pass job ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView postedBy, jobTitle, jobLocation, postTime, badgeNew;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            postedBy = itemView.findViewById(R.id.postedBy);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobLocation = itemView.findViewById(R.id.jobLocation);
            postTime = itemView.findViewById(R.id.postTime);
            badgeNew = itemView.findViewById(R.id.badgeNew);
        }
    }
}
