package com.example.pauseapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;

public class FriendProfileActivity extends AppCompatActivity {

    private BarChart barChart;
    private ArrayList<BarEntry> dailyData,monthlyData,quarterlyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        barChart = findViewById(R.id.barChart);

        Button dailyButton = findViewById(R.id.dailyButton);
        Button monthlyButton = findViewById(R.id.monthlyButton);
        Button trimestralButton = findViewById(R.id.trimestralButton);

        initializeData();
        setupChart();
        updateChart(dailyData);

        dailyButton.setOnClickListener(v -> updateChart(dailyData));
        monthlyButton.setOnClickListener(v -> updateChart(monthlyData));
        trimestralButton.setOnClickListener(v -> updateChart(quarterlyData));
    }

    private void initializeData() {
        dailyData = new ArrayList<>();
        dailyData.add(new BarEntry(1, 75));
        dailyData.add(new BarEntry(1, 78));
        dailyData.add(new BarEntry(2, 55));
        dailyData.add(new BarEntry(3, 42));
        dailyData.add(new BarEntry(4, 27));
        dailyData.add(new BarEntry(5,66));
        dailyData.add(new BarEntry(6,68));
        dailyData.add(new BarEntry(7,10));

        monthlyData = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            monthlyData.add(new BarEntry(i, (float) (Math.random() * 100)));
        }

        quarterlyData = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            quarterlyData.add(new BarEntry(i, (float) (Math.random() * 50)));
        }
    }

    private void setupChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        barChart.getAxisRight().setEnabled(false);
    }

    private void updateChart(ArrayList<BarEntry> data) {
        BarDataSet dataSet = new BarDataSet(data, "Niveles de Estrés");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.invalidate();
    }
}
