package com.example.pauseapp.otros;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.example.pauseapp.R;
import com.example.pauseapp.api.*;
import com.example.pauseapp.model.Alert;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AlertWorker extends Worker {

    public AlertWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        AuthApiService apiService = RetrofitClient.getClient().create(AuthApiService.class);

        Call<List<Alert>> call = apiService.getAllAlerts();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<Alert>> call, Response<List<Alert>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Alert alert : response.body()) {
                        showNotification(alert.getTitle(), alert.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Alert>> call, Throwable t) {
                // Log de error
            }
        });

        return Result.success();
    }

    private void showNotification(String title, String message) {
        Context context = getApplicationContext();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "alert_channel")
                .setSmallIcon(R.drawable.actividad4_2)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }


}

