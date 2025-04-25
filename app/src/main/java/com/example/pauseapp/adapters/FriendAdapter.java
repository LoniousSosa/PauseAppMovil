package com.example.pauseapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pauseapp.R;
import com.example.pauseapp.model.User;
import com.example.pauseapp.model.UserRelation;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<UserRelation> userRelations;
    private Long currentUserId;
    private final int TYPE_REQUEST = 0;
    private final int TYPE_ACCEPTED = 1;

    public interface OnFriendActionListener {
        void onAccept(UserRelation relation);
        void onDeny(UserRelation relation);
        void onOpenProfile(UserRelation relation);
        void onChat(UserRelation relation);
    }

    private OnFriendActionListener listener;

    public FriendAdapter(List<UserRelation> userRelations, Long currentUserId, OnFriendActionListener listener) {
        this.userRelations = userRelations;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    public FriendAdapter(List<UserRelation> userRelations, Long currentUserId) {
        this.userRelations = userRelations;
        this.currentUserId = currentUserId;
    }
    @Override
    public int getItemViewType(int position) {

        UserRelation relation = userRelations.get(position);
        if ("PENDING".equalsIgnoreCase(relation.getStatus()) &&
                relation.getReceiver().getId() == (currentUserId)) {
            return TYPE_REQUEST;
        } else {
            return TYPE_ACCEPTED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_REQUEST) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_add, parent, false);
            return new RequestViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_added_friend, parent, false);
            return new AcceptedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        UserRelation relation = userRelations.get(position);
        User user = relation.getSender().getId() == (currentUserId) ? relation.getReceiver() : relation.getSender();

        if (holder instanceof RequestViewHolder) {
            RequestViewHolder vh = (RequestViewHolder) holder;
            vh.name.setText(user.getUsername());
            vh.accept.setOnClickListener(v -> listener.onAccept(relation));
            vh.deny.setOnClickListener(v -> listener.onDeny(relation));
        } else if (holder instanceof AcceptedViewHolder) {
            AcceptedViewHolder vh = (AcceptedViewHolder) holder;
            vh.name.setText(user.getUsername());
            vh.status.setText(relation.getStatus());
            vh.profile.setOnClickListener(v -> listener.onOpenProfile(relation));
            vh.chat.setOnClickListener(v -> listener.onChat(relation));
        }
    }

    @Override
    public int getItemCount() {
        return userRelations.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        Button accept, deny;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameAdd);
            accept = itemView.findViewById(R.id.seeProfileButton);
            deny = itemView.findViewById(R.id.openChatButton);
        }
    }

    static class AcceptedViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        Button profile, chat;

        public AcceptedViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.solicitudDe);
            status = itemView.findViewById(R.id.nameAdd);
            profile = itemView.findViewById(R.id.seeProfileButton);
            chat = itemView.findViewById(R.id.openChatButton);
        }
    }

    public void updateData(List<UserRelation> newList) {
        userRelations = newList;
        notifyDataSetChanged();
    }
}
