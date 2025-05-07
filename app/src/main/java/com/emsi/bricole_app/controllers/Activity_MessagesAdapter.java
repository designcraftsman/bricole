package com.emsi.bricole_app.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.emsi.bricole_app.R;
import com.emsi.bricole_app.models.Chat;
import com.emsi.bricole_app.models.Job;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class Activity_MessagesAdapter extends RecyclerView.Adapter<Activity_MessagesAdapter.ChatViewHolder> {

    Context context;
    List<Chat> messageList;
    private static final String IMAGE_BASE_URL = "http://10.0.2.2:8080/images/profile/";
    OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public Activity_MessagesAdapter(Context context, List<Chat> messageList, OnChatClickListener listener) {
        this.context = context;
        this.messageList = messageList;
        this.listener = listener;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_chat, parent, false);
        return new ChatViewHolder(view);
    }

    public void updateList(List<Chat> newList) {
        this.messageList = newList;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        Chat message = messageList.get(position);
        holder.senderName.setText(message.name);
        holder.lastMessage.setText(message.lastMessage);
        holder.messageTime.setText(message.date);
        try {
            String imageUrl = IMAGE_BASE_URL +
                    URLEncoder.encode(message.profilePictureUrl, "UTF-8") +
                    "?t=" + System.currentTimeMillis();

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.avatar)
                    .into(holder.avatar);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView senderName, lastMessage, messageTime;
        ImageView avatar;

        public ChatViewHolder(View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            messageTime = itemView.findViewById(R.id.messageTime);
            avatar = itemView.findViewById(R.id.avatar);
        }
    }
}
