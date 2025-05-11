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
import com.example.pauseapp.model.UserResponse;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.VH> {

    public interface OnSuggestionClickListener {
        void onAdd(UserResponse user);
        void onViewProfile(UserResponse user);
    }

    private final List<UserResponse> data;
    private final OnSuggestionClickListener listener;

    public SuggestionAdapter(
            @NonNull List<UserResponse> data,
            long myselfId,
            @NonNull OnSuggestionClickListener listener
    ) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_posiblefriend, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        UserResponse u = data.get(pos);

        // Ponemos el texto fijo y el nombre dinámico
        h.solicitudDe.setText("Usuario");
        h.nameAdd.setText(u.getUsername());

        // Botones: aceptar = añadir, perfil = ver perfil
        h.btnAdd.setText("Añadir");
        h.btnProfile.setText("Perfil");

        h.btnAdd.setOnClickListener(v -> listener.onAdd(u));
        h.btnProfile.setOnClickListener(v -> listener.onViewProfile(u));

        // Avatar genérico
        h.avatar.setImageResource(R.drawable.friend_icon);
    }

    @Override public int getItemCount() {
        return data.size();
    }

    /** Actualiza la lista y refresca */
    public void updateData(@NonNull List<UserResponse> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView avatar;
        final TextView solicitudDe, nameAdd;
        final Button   btnAdd, btnProfile;

        VH(View item) {
            super(item);
            avatar      = item.findViewById(R.id.newFriendAddImage);
            solicitudDe = item.findViewById(R.id.solicitudDe);
            nameAdd     = item.findViewById(R.id.nameAdd);
            btnAdd      = item.findViewById(R.id.seeProfileButton);
            btnProfile  = item.findViewById(R.id.openChatButton);
        }
    }
}