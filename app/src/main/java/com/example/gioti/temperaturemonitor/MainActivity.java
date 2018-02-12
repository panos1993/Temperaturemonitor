package com.example.gioti.temperaturemonitor;
import android.bluetooth.BluetoothAdapter;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private static final int REQUEST_ENABLE_BT = 1;
    TextView tvTemperature;
    Bluetooth bt;
    Toolbar mToolBar;
    Menu mMenu;
   // Button reconButton;
    BluetoothAdapter bluetoothAdapter;
    private LineChart chart;
    LineDataSet set1;
    LineData data;
    ArrayList<Entry> tempValue;
    ArrayList <ILineDataSet> dataSets;
    Map<String, String> temp = new HashMap<>();
    private static StringBuilder sb = new StringBuilder();
    float time=10f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // create class object
        //reconButton = findViewById(R.id.reconButton);
        tvTemperature = findViewById(R.id.tvTemperature);
        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        //create a chart
        InitializeChart();
        setStyleChart();
        //Initialize bluetooth with Handler and check for exception
        bt = new Bluetooth(mHandler);
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            connectService();
        } catch (Exception e){
            Log.e("BLUETOOTH", "BT adapter is not available", e);
        }
        //call the function connectService to start the communication with bluetooth device (Arduino)

    }



    //Initialize and create Chart with the first element which is 0,0.
    public void InitializeChart(){
        chart = findViewById(R.id.chart);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        //setStyleChart();
        tempValue = new ArrayList<>();
        tempValue.add(new Entry(0,0f));
        set1 = new LineDataSet(tempValue,"Temperature Monitor");
        set1.setFillAlpha(110);
        dataSets=new ArrayList<>();
        dataSets.add(set1);
        data = new LineData(dataSets);
        chart.setData(data);
    }

    //Give style in my chart how it will look like
    public void setStyleChart(){
        chart.setDrawBorders(false);
        chart.setMaxVisibleValueCount(1000);
        Description d = new Description();
        d.setText("Temperature Monitor");
        chart.setDescription(d);
        chart.setBackgroundColor(Color.rgb(27,120,196));
        chart.setBorderColor(Color.RED);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false); // if disabled, scaling can be done on x- and y-axis separately
        chart.setDrawGridBackground(false);
        chart.setMaxHighlightDistance(300);
        chart.setBorderWidth(3f);
        XAxis x = chart.getXAxis();
        x.setEnabled(true);
        YAxis y = chart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);
        chart.getAxisRight().setEnabled(true);
        // add data
        chart.getLegend().setEnabled(false);
        chart.animateXY(2000, 2000);
        // dont forget to refresh the drawing
        chart.invalidate();
    }

    //Connect with bluetooth.
    public void connectService() {

        try {
            if (bluetoothAdapter.isEnabled()) {//check if bluetooth adapter in mobile telephone is enabled.
                bt.start();
                bt.connectDevice("HC-05");///device name
                mMenu.findItem(R.id.action_reconnect).setEnabled(false);
               // Log.d("BLUETOOTH", "Btservice started - listening");

            } else { //if bluetooth adapter is disabled ask from user to enabled it.
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                mMenu.findItem(R.id.action_reconnect).setEnabled(true);
            }
        } catch (Exception e) {
            Log.e("BLUETOOTH", "Unable to start bt ", e);

        }
    }
    //MenuBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu,menu);
        this.mMenu = menu;
        //menu.findItem(R.id.action_reconnect).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_reconnect){
            connectService();
        }
        if(item.getItemId() == R.id.action_open_bluetooth_settings){
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings","com.android.settings.bluetooth.BluetoothSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity( intent);
        }
        return super.onOptionsItemSelected(item);
    }
    //set data to graph
    public void setData(Float time,Float value) {

        set1.addEntry(new Entry(time,value));
        dataSets.add(set1);

        data.addDataSet(dataSets.get(dataSets.size()-1));
        chart.setData(data);
        chart.notifyDataSetChanged();
        chart.invalidate();

    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    if(msg.arg1 == 4 || msg.arg1==1){
                        if(mMenu!=null){
                            mMenu.findItem(R.id.action_reconnect).setEnabled(true);
                        }
                        //reconButton.setEnabled(true);
                       // mMenu.findItem(R.id.action_reconnect).setEnabled(true);
                    }
                    if(msg.arg1 == 3){
                        mMenu.findItem(R.id.action_reconnect).setEnabled(false);
                        //reconButton.setEnabled(false);
                    }
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    temp.put(Float.toString(time),bt.tempData);
                    tvTemperature.setText(bt.tempData + "°C");
                    time = time+10;
                    setData(time,Float.parseFloat(bt.tempData));
                    break;
                case Bluetooth.MESSAGE_DEVICE_NAME:
                    Log.d(TAG, "MESSAGE_DEVICE_NAME " + msg);
                    break;
                case Bluetooth.MESSAGE_TOAST:
                    Log.d(TAG, "MESSAGE_TOAST " + msg);
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

}
