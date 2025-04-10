package com.example.pauseapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pauseapp.R;
import com.example.pauseapp.model.ActivityResponse;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<ActivityResponse> activities;

    public ActivityAdapter(List<ActivityResponse> activities) {
        this.activities = activities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityResponse activity = activities.get(position);
        holder.tituloActividad.setText(activity.getName());
        holder.descriptionActivity.setText(activity.getDescription());

        // Cargar imagen con Glide
        Glide.with(holder.itemView.getContext())
                .load(activity.getThumbnailUrl())
                .placeholder(R.drawable.actividad4_2)
                .into(holder.newFriendAddImage);
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tituloActividad, descriptionActivity;
        ImageView newFriendAddImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tituloActividad = itemView.findViewById(R.id.tituloActividad);
            descriptionActivity = itemView.findViewById(R.id.descripctionActivity);
            newFriendAddImage = itemView.findViewById(R.id.newFriendAddImage);
        }
    }
}
