package com.example.pauseapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pauseapp.R;
import com.example.pauseapp.api.AuthApiService;
import com.example.pauseapp.api.RetrofitClient;
import com.example.pauseapp.model.StressLevelResponse;
import com.example.pauseapp.model.UserResponse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendProfileActivity extends MenuFunction {

    private static final String PREFS_NAME = "PauseAppPrefs";
    private static final String KEY_TOKEN  = "auth_token";

    private BarChart barChart;
    private TextView stressText;
    private ArrayList<BarEntry> dailyData;
    private AuthApiService authApiService;
    private long otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        setupNavigationNoBottom();

        barChart    = findViewById(R.id.barChart);
        stressText  = findViewById(R.id.stressText);

        Button dailyButton      = findViewById(R.id.dailyButton);
        Button monthlyButton    = findViewById(R.id.monthlyButton);
        Button trimestralButton = findViewById(R.id.trimestralButton);

        authApiService = RetrofitClient.getClient().create(AuthApiService.class);
        otherUserId    = getIntent().getLongExtra("other_user_id", 0L);

        setupChart();
        initializeData();
        fetchUserData();

        dailyButton.setOnClickListener(view -> updateChart(dailyData, "days"));
        // Si quieres mes/trimestre luego puedes implementar similar,
        // por ahora dejamos vacíos:
        monthlyButton.setOnClickListener(view -> updateChart(new ArrayList<>(), "months"));
        trimestralButton.setOnClickListener(view -> updateChart(new ArrayList<>(), "quarters"));
    }

    private void fetchUserData() {
        authApiService.getUserById(otherUserId, "Bearer " + getAuthToken())
                .enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UserResponse> call,
                                           @NonNull Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse user = response.body();
                            stressText.setText(getString(R.string.stress_level) + " " + user.getUsername());
                            buildChartData(user.getStressLevels());
                            updateChart(dailyData, "days");
                        } else {
                            Toast.makeText(FriendProfileActivity.this,
                                    "Error al cargar datos de usuario",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<UserResponse> call, @NonNull Throwable t) {
                        Toast.makeText(FriendProfileActivity.this,
                                "Fallo de red: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void buildChartData(List<StressLevelResponse> stressLevels) {
        // 1) Inicializar mapa de lista vacía por cada día
        Map<DayOfWeek, List<Float>> nivelesPorDia = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek d : DayOfWeek.values()) {
            nivelesPorDia.put(d, new ArrayList<>());
        }

        // 2) Calcular lunes y domingo de la semana actual
        LocalDate today  = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);
        LocalDate sunday = monday.plusDays(6);

        // 3) Filtrar y agrupar
        if (stressLevels != null) {
            for (StressLevelResponse slr : stressLevels) {
                LocalDate date = LocalDateTime.parse(slr.getDate()).toLocalDate();
                if (!date.isBefore(monday) && !date.isAfter(sunday)) {
                    nivelesPorDia.get(date.getDayOfWeek()).add(slr.getLevel());
                }
            }
        }

        // 4) Calcular media diaria y crear BarEntry (Lun=1…Dom=7)
        dailyData = new ArrayList<>();
        int x = 1;
        for (DayOfWeek dow : DayOfWeek.values()) {
            // Solo queremos LUN..DOM
            if (dow.getValue() >= DayOfWeek.MONDAY.getValue() &&
                    dow.getValue() <= DayOfWeek.SUNDAY.getValue()) {

                List<Float> list = nivelesPorDia.get(dow);
                float media = 0f;
                if (!list.isEmpty()) {
                    float sum = 0f;
                    for (float v : list) sum += v;
                    media = sum / list.size();
                }
                dailyData.add(new BarEntry(x++, media));
            }
        }
    }

    private void initializeData() {
        dailyData = new ArrayList<>();
    }

    private void setupChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);

        YAxis left = barChart.getAxisLeft();
        left.setDrawGridLines(false);
        left.setAxisMinimum(0f);
        left.setAxisMaximum(100f);
        left.setLabelCount(6, true);
    }

    private void updateChart(ArrayList<BarEntry> data, String type) {
        String[] daysOfWeek = {"Lun","Mar","Mié","Jue","Vie","Sáb","Dom"};
        String[] months     = {"Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep","Oct","Nov","Dic"};
        String[] quarters   = {"1er","2º","3º","4º"};

        String[] labels;
        if ("days".equals(type)) {
            labels = daysOfWeek;
        } else if ("months".equals(type)) {
            labels = months;
        } else if ("quarters".equals(type)) {
            labels = quarters;
        } else {
            labels = new String[]{};
        }


        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelCount(labels.length, false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override public String getFormattedValue(float value) {
                int idx = (type.equals("days") ? (int)value - 1 : (int)value);
                return (idx >= 0 && idx < labels.length) ? labels[idx] : "";
            }
        });
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);

        YAxis left = barChart.getAxisLeft();
        left.setTextColor(Color.WHITE);
        left.setTextSize(12f);

        BarDataSet set = new BarDataSet(data, "Niveles de Estrés");
        set.setColor(Color.WHITE);
        set.setDrawValues(false);

        barChart.setData(new BarData(set));
        barChart.invalidate();
    }

    private String getAuthToken() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .getString(KEY_TOKEN, "");
    }
}