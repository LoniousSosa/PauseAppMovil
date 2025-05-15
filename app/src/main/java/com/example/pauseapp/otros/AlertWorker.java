package com.example.pauseapp.otros;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.Alert;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class AlertWorker extends Worker {
    private static final String CHANNEL_ID = "alerst_channel";
    private static final int NOTIFICATION_ID = 100;

    public AlertWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        createNotificationChannel();

        String token = getApplicationContext()
                .getSharedPreferences("PauseAppPrefs", Context.MODE_PRIVATE)
                .getString("auth_token", "");

        AuthApiService api = RetrofitClient.getClient()
                .create(AuthApiService.class);

        Call<List<Alert>> call = api.getAllAlerts("Bearer " + token);
        try {
            Response<List<Alert>> resp = call.execute();
            if (resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
                // Puedes seleccionar la alerta que prefieras, aquí la primera
                Alert alerta = resp.body().get(0);
                showNotification(alerta.getTitle(), alerta.getMessage());
            }
            return Result.success();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alertas PauseApp";
            String description = "Canal para notificaciones de alertas";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager manager = getApplicationContext()
                    .getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)      // tu icono monocromo de 24dp
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        // Decodificamos el logo full-color desde resources
        Bitmap largeIconBitmap = BitmapFactory.decodeResource(
                getApplicationContext().getResources(),
                R.drawable.logo_definitivo);             // tu PNG a color

        // Lo asignamos como large icon
        builder.setLargeIcon(largeIconBitmap);

        NotificationManager manager = (NotificationManager)
                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(NOTIFICATION_ID, builder.build());
        }
    }

}