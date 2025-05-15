package com.example.pauseapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pauseapp.R;
import com.example.pauseapp.activities.PresentationActivity;
import com.example.pauseapp.model.ActivityResponse;
import java.util.ArrayList;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<ActivityResponse> activities;

    public ActivityAdapter(List<ActivityResponse> activities) {
        this.activities = new ArrayList<>(activities != null ? activities : new ArrayList<>());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityResponse activity = activities.get(position);
        holder.tituloActividad.setText(activity.getName());
        holder.descriptionActivity.setText(activity.getType().getName().toUpperCase());

        Glide.with(holder.itemView.getContext())
                .load(activity.getThumbnailUrl())
                .placeholder(R.drawable.actividad4_2)
                .into(holder.newFriendAddImage);

        holder.itemView.setOnClickListener(view -> {
            // Crea el Intent usando el contexto del itemView
            Context ctx = view.getContext();
            Intent intent = new Intent(ctx, PresentationActivity.class);
            intent.putExtra("ACTIVITY_ID", activity.getId());
            ctx.startActivity(intent);
        });
        holder.seeProfileButton.setOnClickListener(view -> {
            Context ctx = view.getContext();
            Intent intent = new Intent(ctx, PresentationActivity.class);
            intent.putExtra("ACTIVITY_ID", activity.getId());
            ctx.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return activities.size();
    }

    /**
     * Actualiza la lista de actividades mostradas y refresca el RecyclerView.
     * @param newActivities Nueva lista de ActivityResponse
     */
    public void updateData(List<ActivityResponse> newActivities) {
        this.activities.clear();
        if (newActivities != null) {
            this.activities.addAll(newActivities);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tituloActividad, descriptionActivity;
        ImageView newFriendAddImage;
        Button seeProfileButton;

        public ViewHolder(View itemView) {
            super(itemView);
            tituloActividad = itemView.findViewById(R.id.tituloActividad);
            descriptionActivity = itemView.findViewById(R.id.descripctionActivity);
            newFriendAddImage = itemView.findViewById(R.id.newFriendAddImage);
            seeProfileButton = itemView.findViewById(R.id.seeProfileButton);
        }
    }
}

