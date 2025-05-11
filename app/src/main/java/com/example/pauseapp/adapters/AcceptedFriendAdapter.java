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

/**
 * Adapter para mostrar la lista de amigos ya aceptados.
 * Utiliza directamente UserResponse en lugar de UserRelation.
 */
public class AcceptedFriendAdapter extends RecyclerView.Adapter<AcceptedFriendAdapter.VH> {

    public interface OnFriendClickListener {
        void onOpenProfile(UserResponse u);
        void onChat(UserResponse u);
    }

    private final List<UserResponse> data;
    private final OnFriendClickListener listener;

    public AcceptedFriendAdapter(@NonNull List<UserResponse> data,
                                 @NonNull OnFriendClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_added_friend, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        UserResponse u = data.get(pos);
        // Mostrar nombre del amigo
        h.solicitudDe.setText(u.getUsername());
        // Ocultar el TextView que antes mostraba el estado
        h.nameAdd.setVisibility(View.GONE);

        // Botones para perfil y chat
        h.btnOne.setText("Perfil");
        h.btnTwo.setText("Chat");
        h.btnOne.setOnClickListener(v -> listener.onOpenProfile(u));
        h.btnTwo.setOnClickListener(v -> listener.onChat(u));

        // Avatar genérico
        h.avatar.setImageResource(R.drawable.friend_icon);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Actualiza los datos y refresca la lista.
     */
    public void updateData(@NonNull List<UserResponse> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView solicitudDe, nameAdd;
        Button btnOne, btnTwo;

        VH(View itemView) {
            super(itemView);
            avatar      = itemView.findViewById(R.id.newFriendAddImage);
            solicitudDe = itemView.findViewById(R.id.solicitudDe);
            nameAdd     = itemView.findViewById(R.id.nameAdd);
            btnOne      = itemView.findViewById(R.id.seeProfileButton);
            btnTwo      = itemView.findViewById(R.id.openChatButton);
        }
    }
}

