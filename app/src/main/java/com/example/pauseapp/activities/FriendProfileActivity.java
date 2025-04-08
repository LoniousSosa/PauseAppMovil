package com.example.pauseapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import com.example.pauseapp.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class FriendProfileActivity extends MenuFunction {

    private BarChart barChart;
    private ArrayList<BarEntry> dailyData,monthlyData, trimestralData;
    private int dayCounter, monthCounter,trimestralCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        setupNavigationNoBottom();

        barChart = findViewById(R.id.barChart);

        Button dailyButton = findViewById(R.id.dailyButton);
        Button monthlyButton = findViewById(R.id.monthlyButton);
        Button trimestralButton = findViewById(R.id.trimestralButton);

        initializeData();
        setupChart();
        updateChart(dailyData, "days");

        dailyButton.setOnClickListener(v -> updateChart(dailyData,"days"));
        monthlyButton.setOnClickListener(v -> updateChart(monthlyData,"months"));
        trimestralButton.setOnClickListener(v -> updateChart(trimestralData,"quarters"));
    }

    private void initializeData() {

        /**
         * Cuando se cambie de forma dinámica, el momento en el que el contador de
         * dias llegue a 8, los datos del monthly data se reiniciarán
         */

        dayCounter++;

        dailyData = new ArrayList<>();
        dailyData.add(new BarEntry(1, 75));
        dailyData.add(new BarEntry(1, 78));
        dailyData.add(new BarEntry(2, 55));
        dailyData.add(new BarEntry(3, 42));
        dailyData.add(new BarEntry(4, 27));
        dailyData.add(new BarEntry(5,66));
        dailyData.add(new BarEntry(6,68));
        dailyData.add(new BarEntry(7,10));


        /**
         * Cuando se cambie de forma dinámica, el momento en el que el contador de
         * meses llegue a 13, los datos del monthly data se reiniciarán
         */

        monthCounter++;
        monthlyData = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            monthlyData.add(new BarEntry(i, (float) (Math.random() * 100)));
        }

        /**
         * Cuando se cambie de forma dinámica, el momento en el que el contador de
         * meses llegue a 12, los datos del monthly data se reiniciarán. Además,
         * cada 3 meses, un nuevo dato se calculará pillando los tres meses
         * correspondientes del array de los meses
         */

        trimestralCounter++;
        trimestralData = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            trimestralData.add(new BarEntry(i, (float) (Math.random() * 50)));
        }
    }

    private void setupChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);

    }

    private void updateChart(ArrayList<BarEntry> data , String type) {
        String[] daysOfWeek = {"Lun", "Mar", "Mié", "Jue", "Hoy", "Sáb", "Dom"};
        String[] months = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        String[] trimestres = {"Primero", "Segundo", "Tercero", "Cuarto"};

        String[] labels;
        XAxis xAxis = barChart.getXAxis();
        if (type.equals("days")){
            labels = daysOfWeek;
        } else if (type.equals("months")) {
            labels = months;
        }else if (type.equals("quarters")) {
            labels = trimestres;
        }else
            labels = new String[]{};

        xAxis.setLabelCount(labels.length, false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (type.equals("days")){
                     index = (int) value -1;
                }
                if (index >= 0 && index < labels.length) {
                    return labels[index];
                }
                return "";
            }
        });

        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(12f);
        barChart.getAxisLeft().setTextColor(Color.WHITE);
        barChart.getAxisLeft().setTextSize(12f);

        BarDataSet dataSet = new BarDataSet(data, "Niveles de Estrés");
        dataSet.setColor(Color.WHITE);
        dataSet.setDrawValues(false);
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();
    }
}
