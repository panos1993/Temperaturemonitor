package com.example.gioti.temperaturemonitor;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.github.mikephil.charting.charts.LineChart;

/**
 * This class is called when we choose to open a file from the main activity
 * it opens the saved measurements that were chosen and draws them on the chart
 */
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

        //create toolbar menu
        Toolbar mToolBar = findViewById(R.id.toolbar2);
        setSupportActionBar(mToolBar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // create an object of "ShowMaterlialDialog" class
        smd = new ShowMaterialDialog();
        smd.initializeData(OpenSaveCharts.this);
        // initialize chart
        sChart = findViewById(R.id.chart2);
        mChart = new ManageChart();
        mChart.setStyleChart(sChart);
        //mChart.InitializeChart(sChart);
        fm = new FileManagement();
    }

    //create and initialize menu options
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
        mMenu.findItem(R.id.action_manage_file).setVisible(false);
        mMenu.findItem(R.id.action_about_us).setVisible(true);
        mMenu.findItem(R.id.action_stop).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //when back arrow pressed in tool bar run this if
        if (item.getItemId() == android.R.id.home ) {
            onBackPressed();
        }
        if(item.getItemId() == R.id.action_clear_graph){
            //clears data graph from previous measurements
            mMenu.findItem(R.id.action_delete_measurement).setEnabled(true);
            mMenu.findItem(R.id.action_clear_graph).setEnabled(false);
            mChart.resetGraph(sChart);
            mChart.cleanListsForSaveChart();
            smd.initializeData(OpenSaveCharts.this);
        }
        if(item.getItemId() == R.id.action_open_measurement){
            // the function ManageOpenFile is called by smd object and we can choose the measures that chart will show
            smd.ManageOpenAndDeleteFile(OpenSaveCharts.this,sChart,mChart,true,mHandler,mMenu);
        }
        // if we press "about us" button, some informations about us will be appeared
        if(item.getItemId() == R.id.action_about_us) {
            ShowMaterialDialog.aboutAsFunction(OpenSaveCharts.this);
        }
        if(item.getItemId() == R.id.action_delete_measurement){
            smd.ManageOpenAndDeleteFile(OpenSaveCharts.this,sChart,mChart,false,mHandler,mMenu);
        }
        if(item.getItemId() == R.id.action_delete_all_measurement){
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_delete)
                    .setCancelable(false)
                    .setTitle("Delete All Measurements")
                    .setMessage("\t\tAre you sure??")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FileManagement.deleteAllFromFile(this);
                        onBackPressed();
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        //do nothing.
                    })
                    .show();
        }
        return true;   // when we choose sth from menu it returns true. Otherwise, it returns false
    }
    Handler mHandler = new Handler(message -> {
        switch (message.what){
            case ShowMaterialDialog.MESSAGE_KILL_OpenSaveCharts_ACTIVITY:
                onBackPressed();
                break;
        }

        return false;
    });
    /**
     * This function is called automatically when back button is pressed.
     * Go one page back.
     * */
    @Override
    public void onBackPressed() {
        mChart.cleanListsForSaveChart();
        smd.initializeData(OpenSaveCharts.this);
        finishAndRemoveTask();
        Intent i =new Intent(this,MainActivity.class);
        startActivity(i);

    }

}
