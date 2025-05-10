package com.example.pauseapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pauseapp.R;
import com.example.pauseapp.model.UserRelation;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    public interface OnFriendActionListener {
        void onAccept(UserRelation relation);
        void onDeny(UserRelation relation);
        void onOpenProfile(UserRelation relation);
        void onChat(UserRelation relation);
    }

    private final List<UserRelation> data;
    private final long myselfId;
    private final OnFriendActionListener listener;
    private final int layoutRes;

    public FriendAdapter(@NonNull List<UserRelation> data,
                         long myselfId,
                         @NonNull OnFriendActionListener listener,
                         int layoutRes) {
        this.data       = data;
        this.myselfId   = myselfId;
        this.listener   = listener;
        this.layoutRes  = layoutRes;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(layoutRes, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        UserRelation r = data.get(pos);
        // Determinar el "otro usuario"
        boolean iamSender = r.getSender().getId() == myselfId;
        String otherName = iamSender
                ? r.getReceiver().getUsername()
                : r.getSender().getUsername();

        if (layoutRes == R.layout.item_friend_add) {
            // Layout de solicitudes
            h.solicitudDe.setText("Solicitud de");
            h.nameAdd.setText(otherName);
            h.btnOne.setText("Aceptar");
            h.btnTwo.setText("Denegar");
            h.btnOne.setOnClickListener(v -> listener.onAccept(r));
            h.btnTwo.setOnClickListener(v -> listener.onDeny(r));
        } else {
            // Layout de amigos añadidos
            h.solicitudDe.setText(otherName);
            // mostrar estado si existe:
            String status = r.getStatus() != null ? r.getStatus() : "";
            h.nameAdd.setText(status);
            h.btnOne.setText("Perfil");
            h.btnTwo.setText("Chat");
            h.btnOne.setOnClickListener(v -> listener.onOpenProfile(r));
            h.btnTwo.setOnClickListener(v -> listener.onChat(r));
        }

        // Avatar genérico
        h.avatar.setImageResource(R.drawable.friend_icon);
    }

    @Override public int getItemCount() {
        return data.size();
    }

    /** Actualiza la lista y refresca */
    public void updateData(@NonNull List<UserRelation> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    public List<UserRelation> getData() {
        return data;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView solicitudDe, nameAdd;
        Button btnOne, btnTwo;

        ViewHolder(View itemView) {
            super(itemView);
            avatar      = itemView.findViewById(R.id.newFriendAddImage);
            solicitudDe = itemView.findViewById(R.id.solicitudDe);
            nameAdd     = itemView.findViewById(R.id.nameAdd);
            btnOne      = itemView.findViewById(R.id.seeProfileButton);
            btnTwo      = itemView.findViewById(R.id.openChatButton);
        }
    }
}

