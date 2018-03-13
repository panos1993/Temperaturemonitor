package com.example.gioti.temperaturemonitor;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.github.mikephil.charting.charts.LineChart;

public class OpenSaveCharts extends AppCompatActivity {
    LineChart sChart;
    ManageChart mChart;
    FileManagement fm;
    static  Menu mMenu;
    boolean hasDataOnGraph=false;
    ShowMaterialDialog smd;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_save_charts);

        //toolbar initialize
        Toolbar mToolBar = findViewById(R.id.toolbar2);
        setSupportActionBar(mToolBar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        smd = new ShowMaterialDialog();
        smd.initializeData(OpenSaveCharts.this);
        //chart initialize
        sChart = findViewById(R.id.chart2);
        mChart = new ManageChart();
        fm = new FileManagement();
        mChart.InitializeChart(sChart);
       // mChart.setStyleChart(sChart);
    }

    //MenuBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu,menu);
        mMenu = menu;
        if(!hasDataOnGraph){
            mMenu.findItem(R.id.action_clear_graph).setEnabled(false);
        }
        mMenu.findItem(R.id.action_usb).setVisible(false);
        mMenu.findItem(R.id.action_bluetooth).setVisible(false);
        mMenu.findItem(R.id.action_open_bluetooth_settings).setVisible(false);
        mMenu.findItem(R.id.action_save_file).setVisible(false);
        mMenu.findItem(R.id.action_open_file).setVisible(false);
        mMenu.findItem(R.id.action_about_us).setVisible(true);
        mMenu.findItem(R.id.action_stop).setVisible(false);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
        }
        if(item.getItemId() == R.id.action_clear_graph){
            mMenu.findItem(R.id.action_clear_graph).setEnabled(false);
            mChart.resetGraph(sChart);
            mChart.cleanListsForSaveChart();
            smd.initializeData(OpenSaveCharts.this);
        }
        if(item.getItemId() == R.id.action_open_measurement){
            smd.ManageOpenFile(OpenSaveCharts.this,sChart,mChart);
        }
        if(item.getItemId() == R.id.action_about_us){
            ShowMaterialDialog.aboutAsFunction(OpenSaveCharts.this);
        }
        // other menu select events may be present here
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        mChart.resetGraph(sChart);
        mChart.cleanListsForSaveChart();
        smd.initializeData(OpenSaveCharts.this);
       finishAndRemoveTask();
    }

}
