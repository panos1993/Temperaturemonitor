package com.example.gioti.temperaturemonitor;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.LineChart;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private static final int REQUEST_ENABLE_BT = 1;
    private static StringBuilder sb = new StringBuilder();
    TextView tvTemperature;
    private Bluetooth bt;
    private Menu mMenu;
    private static BluetoothAdapter bluetoothAdapter;
    public static LineChart chart;
    static ManageChart mChart = new ManageChart();
    private static boolean flag=true;
    private boolean hasDataToSave=false, isConnectedWithBt=false,isConnectedWithUsb=false;

    private static String locationAddress;
    //usb services.
    private SerialConnectionUsb usb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // create class object
        tvTemperature = findViewById(R.id.tvTemperature);
        //initialize toolbar menu
        Toolbar mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);  // enable one extra feature in toolbar. sets a button that transfers us to the previous screen

        //create a chart
        chart = findViewById(R.id.chart);
        mChart.setStyleChart(chart);

        usb= new SerialConnectionUsb(usbHandler,this);

        //Initialize bluetooth via Handler and check for exception
        bt = new Bluetooth(btHandler);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);

        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } catch (Exception e){
            Log.e("BLUETOOTH", "BT adapter is not available", e);
        }


    }

    //manage the buttons bluetooth, usb and stop. set enabled/disabled
    public void manageButton(Boolean b){
        mMenu.findItem(R.id.action_bluetooth).setEnabled(b);
        mMenu.findItem(R.id.action_usb).setEnabled(b);
        mMenu.findItem(R.id.action_stop).setEnabled(!b);

    }

    //Connect with bluetooth.
    public void connectBtService() {

        try {
            if (bluetoothAdapter.isEnabled()) {//check if bluetooth adapter in mobile is enabled.
                bt.start();
                bt.connectDevice();///device name
                Log.d("BLUETOOTH", "Btservice started - listening");

            } else { //if bluetooth adapter is disabled ask from user to set as enabled.
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } catch (Exception e) {
            Log.e("BLUETOOTH", "Unable to start bt ", e);

        }
    }

    /**
     * runs when bluetooth's state have changed
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            assert action != null;
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (bluetoothState) {
                    case BluetoothAdapter.STATE_ON:
                        connectBtService();
                        break;
                }
            }
        }
    };

    //create and initialize menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu,menu);
        this.mMenu = menu;
        mMenu.findItem(R.id.action_save_file).setEnabled(false);
        mMenu.findItem(R.id.action_stop).setEnabled(false);
        if(FileManagement.ReadFromFile(this).size()==0){
            mMenu.findItem(R.id.action_manage_file).setEnabled(false);
        }
        if(!hasDataToSave){
            mMenu.findItem(R.id.action_clear_graph).setEnabled(false);
        }
        mMenu.findItem(R.id.action_open_measurement).setVisible(false);
        mMenu.findItem(R.id.action_delete_measurement).setVisible(false);
        mMenu.findItem(R.id.action_delete_all_measurement).setVisible(false);
        return true;
    }

    /**
     * checking which button has been pressed and running the corresponding mode
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //when back arrow pressed in tool bar run this if
       if (item.getItemId() == android.R.id.home ) {
           onBackPressed();
        }

        // if button "usb connection" has pressed...
        if(item.getItemId() == R.id.action_usb){
            if(hasDataToSave){
                tvAppend(tvTemperature,"0.0 °C");   // initialize text temperature
                usb.connect();
            }else{
                tvAppend(tvTemperature,"0.0 °C");
                usb.connect();
            }
        }
        // if button "bluetooth connection" has pressed...
        if(item.getItemId() == R.id.action_bluetooth){
            if(hasDataToSave){
                tvAppend(tvTemperature,"0.0 °C");
                connectBtService();
            }else{
                tvAppend(tvTemperature,"0.0 °C");
                connectBtService();
            }
        }
        // if button "bluetooth settings" has pressed...
        if(item.getItemId() == R.id.action_open_bluetooth_settings){
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings","com.android.settings.bluetooth.BluetoothSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity( intent);
        }
        // if button "save file" has pressed...
        if(item.getItemId() == R.id.action_save_file){

            if(isConnectedWithBt){
                bt.stop();
            }else if(isConnectedWithUsb){
                usb.Disconnected();
            }

            mMenu.findItem(R.id.action_manage_file).setEnabled(true);
            mMenu.findItem(R.id.action_save_file).setEnabled(false);
            FileManagement.SaveToFile(this);
            Toast.makeText(MainActivity.this, "File saving is successful", Toast.LENGTH_LONG).show();
            FileManagement.deleteAllDataTemperatures();
            hasDataToSave=false;
        }
        // if button "open file" has pressed...
        if(item.getItemId() == R.id.action_manage_file){
            bt.stop();
            if(hasDataToSave) {
                ShowMaterialDialog.mainMaterialDialog(mMenu,MainActivity.this,4, mHandler3);
                hasDataToSave=false;
            }else{
                finishAndRemoveTask();
                Intent i = new Intent(MainActivity.this, OpenSaveCharts.class);
                startActivity(i);
            }
        }
        // if button "clear graph" has pressed...
        if(item.getItemId() == R.id.action_clear_graph){
            if(hasDataToSave){
                ShowMaterialDialog.mainMaterialDialog(mMenu,MainActivity.this,3, mHandler3);
                hasDataToSave=false;
            }
            mChart.resetGraphMainActivity(chart);
            mMenu.findItem(R.id.action_clear_graph).setEnabled(false);
            mMenu.findItem(R.id.action_save_file).setEnabled(false);
        }
        // if button "stop connection" has pressed...
        if(item.getItemId() == R.id.action_stop){
            if(isConnectedWithBt){
                bt.stop();
            }else{
                usb.Disconnected();
            }
            manageButton(true);
        }
        // if button "About Us" has pressed...
        if(item.getItemId() == R.id.action_about_us){
            ShowMaterialDialog.aboutAsFunction(MainActivity.this);
        }
        return true;
    }

    /**
     * Managing bluetooth's events
     * Handler creates a communication tube between two threads for transferring data
     * this handler handles data that Bluetooth class has sent
     */
    Handler btHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            /*
            MESSAGE_STATE_CHANGE = 1;
	        MESSAGE_READ = 2;
	        MESSAGE_WRITE = 3;
	        MESSAGE_DEVICE_NAME = 4;
	        MESSAGE_TOAST = 5;
             */
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);

                    if(msg.arg1 == 4 || msg.arg1==1){   // check if Bluetooth is disconnected or listening to incoming connections

                        if(mMenu!=null){
                            manageButton(true);
                            isConnectedWithBt = false;
                        }

                        if(msg.arg1==4){
                            Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                        }
                    }

                    if(msg.arg1 == 3){ //check if Bluetooth is connected to a remote device.
                        manageButton(false);
                        isConnectedWithBt = true;
                        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show();
                    }

                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    FileManagement.setTemp(bt.tempData, locationAddress); //save data temperature in an arraylist
                    tvAppend(tvTemperature,bt.tempData+"  °C");
                    if(FileManagement.getAllSecond().length>1){
                        mChart.setData(chart);
                    }else{
                        mChart.InitializeChart(chart);
                    }

                    isConnectedWithBt = true;
                    hasDataToSave=true;
                    mMenu.findItem(R.id.action_clear_graph).setEnabled(true);
                    mMenu.findItem(R.id.action_save_file).setEnabled(true);
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
    /**
     * Managing USB events
     * Handler creates a communication tube between two threads for transferring data
     * this handler handles data that SerialConnectionUsb class has sent
     */
    Handler usbHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case SerialConnectionUsb.STATE_CONNECTED:
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_LONG).show();
                    manageButton(false);
                    break;
                case SerialConnectionUsb.MESSAGE_READ:
                    flag= true;
                    FileManagement.setTemp(usb.tempData, locationAddress);
                    tvAppend(tvTemperature,usb.tempData+"  °C");
                    mChart.setData(chart);
                    isConnectedWithUsb = true;
                    hasDataToSave=true;
                    mMenu.findItem(R.id.action_clear_graph).setEnabled(true);
                    mMenu.findItem(R.id.action_save_file).setEnabled(true);
                    break;
                case SerialConnectionUsb.MESSAGE_DISCONNECTED:
                    if(flag){
                        isConnectedWithUsb = false;
                       manageButton(true);
                       Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_LONG).show();
                       flag= false;
                    }
                    break;
            }
            return false;
        }

    });
    /**
     * This handler communicates with the function mainMaterialDialog which is included in ShowMaterialDialog class
     * managing events in material dialogs
     * this handler is responsible for killing the MainActivity class or for transferring us in OpenSaveCharts class
     */

    Handler mHandler3 = new Handler(message -> {
        switch (message.what){
            case ShowMaterialDialog.MESSAGE_KILL_MAIN_ACTIVITY:
                finishAndRemoveTask();      // clears the main activity from the task list
                break;
            case ShowMaterialDialog.MESSAGE_GO_OPEN_SAVE_FILE_ACTIVITY:
                finishAndRemoveTask();
                Intent i = new Intent(MainActivity.this, OpenSaveCharts.class);
                startActivity(i);

        }

        return false;
    });

    /**
     * Refreshing text view data.
     * @param tv any text view which is containing in my class
     * @param text The text which appears in Text view.
     */
    public void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;
        runOnUiThread(() -> ftv.setText(ftext));
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * This function is called automatically when back button is pressed.
     * Going one page back.
     * */
    @Override
    public void onBackPressed() {

        if(isConnectedWithBt){
            bt.stop();
        }else if(isConnectedWithUsb){
            usb.Disconnected();
        }
        mChart.quart=0f;
        if(hasDataToSave) {
            ShowMaterialDialog.mainMaterialDialog(mMenu,this,2,mHandler3);
            hasDataToSave=false;
        }else{
            FileManagement.deleteAllDataTemperatures();
            finishAndRemoveTask();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    /**
     * Getting location address from MapsActivity class
     * @param location Location target.
     */
    public static void setLocation(ArrayList <String> location){
        StringBuilder sb = new StringBuilder();

        for(String pair: location){
            sb.append(pair);
        }
        locationAddress = sb.substring(0, sb.length());

    }
    boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }


}
