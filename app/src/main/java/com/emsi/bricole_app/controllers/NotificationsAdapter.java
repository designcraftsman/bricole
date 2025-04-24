package com.emsi.bricole_app.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.emsi.bricole_app.models.Notification;

import java.util.List;

import com.emsi.bricole_app.R;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<Notification> notifications;
    private OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int notificationId, int position);
    }

    public NotificationsAdapter(List<Notification> notifications, OnDeleteClickListener listener) {
        this.notifications = notifications;
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.component_notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.tvMessage.setText(notification.getMessage());
        holder.tvSender.setText("Sender ID: " + notification.getSenderId());
        holder.tvTimestamp.setText(notification.getCreatedAt());

        holder.btnDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                onDeleteClickListener.onDeleteClick(notification.getId(), currentPosition);
            }

        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void removeItem(int position) {
        notifications.remove(position);
        notifyItemRemoved(position);
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender, tvMessage, tvTimestamp;
        Button btnDelete;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
