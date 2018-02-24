package com.example.gioti.temperaturemonitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;

public class OpenSaveCharts extends AppCompatActivity {
    LineChart sChart;
    ManageChart mChart;
    FileManagement fm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_save_charts);
        sChart = findViewById(R.id.chart2);
        mChart = new ManageChart();
        fm = new FileManagement();
        mChart.InitializeChart(sChart);
        mChart.setStyleChart(sChart);
        mChart.setGraphData(MainActivity.selectedMesurments,sChart,fm,this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
