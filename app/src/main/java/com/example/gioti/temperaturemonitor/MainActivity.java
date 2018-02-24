package com.example.gioti.temperaturemonitor;
import android.annotation.SuppressLint;
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
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private static final int REQUEST_ENABLE_BT = 1;
    @SuppressLint("StaticFieldLeak")
    static TextView tvTemperature;
    private Bluetooth bt;
    private Menu mMenu;
    private static BluetoothAdapter bluetoothAdapter;
    public FileManagement fm= new FileManagement();
    public static LineChart chart;
    ManageChart mChart = new ManageChart();
    private static boolean flag=true;
    static CharSequence[] selectedMesurments;
    //usb services.
    private SerialConnectionUsb usb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create class object
        tvTemperature = findViewById(R.id.tvTemperature);
        Toolbar mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        //create a chart
        chart = findViewById(R.id.chart);
        mChart.InitializeChart(chart);
        mChart.setStyleChart(chart);

        //Initialize bluetooth with Handler and check for exception
        bt = new Bluetooth(mHandler);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        usb= new SerialConnectionUsb(mHandler2,this);
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } catch (Exception e){
            Log.e("BLUETOOTH", "BT adapter is not available", e);
        }


    }

    public void manageButton(Boolean b){
        mMenu.findItem(R.id.action_bluetooth).setEnabled(b);
        mMenu.findItem(R.id.action_usb).setEnabled(b);
        mMenu.findItem(R.id.action_stop).setEnabled(!b);

    }

    //Connect with bluetooth.
    public void connectBtService() {

        try {
            if (bluetoothAdapter.isEnabled()) {//check if bluetooth adapter in mobile telephone is enabled.
                bt.start();
                bt.connectDevice("HC-05");///device name
                Log.d("BLUETOOTH", "Btservice started - listening");

            } else { //if bluetooth adapter is disabled ask from user to enabled it.
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } catch (Exception e) {
            Log.e("BLUETOOTH", "Unable to start bt ", e);

        }
    }
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

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
    //MenuBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu,menu);
        this.mMenu = menu;
        menu.findItem(R.id.action_save_file).setEnabled(false);
        menu.findItem(R.id.action_stop).setEnabled(false);
        if(fm.ReadFromFile(this).size()==0){
            menu.findItem(R.id.action_open_file).setEnabled(false);
        }
        return true;
    }

    /**
     * Ekteleitai kathe fora pou epilegoume ena item tou menou kai analogws poio exoume epileksei ekteleitai diaforetiki diadikasia
     * @param item epistrefei to item pou exoume epileksei apo to UI.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_usb){
            mChart.resetGraph(chart);
            usb.connect();
        }
        if(item.getItemId() == R.id.action_bluetooth){
            mChart.resetGraph(chart);
            connectBtService();
        }

        if(item.getItemId() == R.id.action_open_bluetooth_settings){
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings","com.android.settings.bluetooth.BluetoothSettings");
            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity( intent);
        }
        if(item.getItemId() == R.id.action_save_file){
            bt.stop();
            mMenu.findItem(R.id.action_open_file).setEnabled(true);
            fm.SaveToFile(this);
        }
        if(item.getItemId() == R.id.action_open_file){
            bt.stop();
            ArrayList<Date> date = new ArrayList<>();
            for(SaveModel pair: fm.ReadFromFile(this)){
                date.add(pair.getDatetime());
            }
            new MaterialDialog.Builder(this)
                    .title("Choose one or more temperature mesurment!!!")
                    .items(date)
                    .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                            /**
                             * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                             * returning false here won't allow the newly selected check box to actually be selected
                             * (or the newly unselected check box to be unchecked).
                             * See the limited multi choice dialog example in the sample project for details.
                             **/
                            final Intent i = new Intent(MainActivity.this, OpenSaveCharts.class);
                            selectedMesurments = text;
                            if(text.length>0) {

                                startActivity(i);
                                finish();
                            }
                            return true;
                        }
                    })
                    .positiveText(android.R.string.ok)
                    .negativeText( android.R.string.cancel )
                    .show();

        }
        if(item.getItemId() == R.id.action_stop){
            bt.stop();
            manageButton(true);
        }

        return super.onOptionsItemSelected(item);
    }

    public static CharSequence [] selectedMesurments(){
        return selectedMesurments;
    }
    /**
     * We place data on the graph and refresh the chart to show changes in the UI
     */
//diaxeirizomaste ta event pou sumvainoun apo to bluetooth.
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case Bluetooth.MESSAGE_STATE_CHANGE:
                    Log.d(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    if(msg.arg1 == 4 || msg.arg1==1){
                        if(mMenu!=null){
                            manageButton(true);

                        }
                    }
                    if(msg.arg1 == 3){
                        manageButton(false);
                        mMenu.findItem(R.id.action_save_file).setEnabled(true);
                    }
                    break;
                case Bluetooth.MESSAGE_WRITE:
                    Log.d(TAG, "MESSAGE_WRITE ");
                    break;
                case Bluetooth.MESSAGE_READ:
                    fm.setTemp(bt.tempData);
                    tvAppend(tvTemperature,bt.tempData+"°C");
                    mChart.setData(chart,fm);
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
    //diaxeirizomaste ta event pou sumvainoun apo to usb.
    Handler mHandler2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case SerialConnectionUsb.STATE_CONNECTED:
                    mMenu.findItem(R.id.action_save_file).setEnabled(true);
                    break;
                case SerialConnectionUsb.MESSAGE_DEVICE_NAME:
                    manageButton(false);

                    //Log.d(TAG, "MESSAGE_STATE_CHANGE: " + message.arg1);
                    break;
                case SerialConnectionUsb.MESSAGE_READ:
                    flag= true;
                    //Float temperature = Float.valueOf(usb.tempData);
                    fm.setTemp(usb.tempData);
                    mChart.setData(chart,fm);
                    //tvAppend(tvinformation,"Data transfered " + usb.tempData);
                    tvAppend(tvTemperature,usb.tempData + "°C");
                    break;
                case SerialConnectionUsb.MESSAGE_DISCONNECTED:
                    if(flag){
                       manageButton(true);
                       Toast.makeText(MainActivity.this, "Disconnexted", Toast.LENGTH_LONG).show();
                        flag= false;
                    }
                    break;

            }
            return false;
        }

    });


    /**
     * Ananewnei ta text view toou activity
     * @param tv to text view pou tha ginei refresh
     * @param text h timh pou tha setaristei sto text view
     */
    public void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.setText(ftext);
            }
        });
    }

    /**
     *
     */
    @Override
    protected void onPause() {

        super.onPause();
        bt.stop();
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
