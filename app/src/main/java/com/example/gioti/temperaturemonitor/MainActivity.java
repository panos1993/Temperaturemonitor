package com.example.gioti.temperaturemonitor;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
    ImageView IconTemp;
    Bluetooth bt;
    Button reconButton;
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
        reconButton = findViewById(R.id.reconButton);
        tvTemperature = findViewById(R.id.tvTemperature);
        IconTemp = findViewById(R.id.IconTemp);
        IconTemp.setImageResource(R.drawable.temperature_icon);
        //create a chart
        InitializeChart();
        setStyleChart();
        //Initialize bluetooth with Handler and check for exception
        bt = new Bluetooth(mHandler);
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } catch (Exception e){
            Log.e("BLUETOOTH", "BT adapter is not available", e);
        }
        //call the function connectService to start the communication with bluetooth device (Arduino)
        connectService();

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

    public void connectService() {
        try {
            if (bluetoothAdapter.isEnabled()) {
                bt.start();
                bt.connectDevice("HC-05");///device name
                Log.d("BLUETOOTH", "Btservice started - listening");

            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                bt.start();
                bt.connectDevice("HC-05");///device name
                Log.d("BLUETOOTH", "Btservice started - listening");

            }
        } catch (Exception e) {
            Log.e("BLUETOOTH", "Unable to start bt ", e);

        }
    }
    public void setData(Float time,Float value) {

        set1.addEntry(new Entry(time,value));
        dataSets.add(set1);

        data.addDataSet(dataSets.get(dataSets.size()-1));
        chart.setData(data);

    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    if(msg.arg1 == 4 || msg.arg1==1){
                        reconButton.setEnabled(true);
                    }
                    if(msg.arg1 == 3){
                        reconButton.setEnabled(false);
                    }
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    String strIncom = new String(readBuf, 0, msg.arg1);
                    sb.append(strIncom);
                    int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                    if (endOfLineIndex > 0) {                                            // if end-of-line,
                        String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                        sb.delete(0, sb.length());                                      // and clear
                        Log.d("READ_FROM_ARDUINO", sbprint);
                        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
                        temp.put(Float.toString(time),sbprint);
                        setData(time,Float.parseFloat(sbprint));
                        tvTemperature.setText(sbprint + "°C");
                        time = time+10;

                    }
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

    public void reconnectClicked(View view) {connectService();}
}
