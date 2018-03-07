package com.example.gioti.temperaturemonitor;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mikephil.charting.charts.LineChart;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private static final int REQUEST_ENABLE_BT = 1;
    @SuppressLint("StaticFieldLeak")
    static TextView tvTemperature;
    private Bluetooth bt;
    private Menu mMenu;
    private static BluetoothAdapter bluetoothAdapter;
    public static LineChart chart;
    static ManageChart mChart = new ManageChart();
    private static boolean flag=true;
    private boolean hasDataForSave=false;

    private static String locationAddress;
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
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
    //MenuBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflater = getMenuInflater();
        mMenuInflater.inflate(R.menu.my_menu,menu);
        this.mMenu = menu;
        mMenu.findItem(R.id.action_save_file).setEnabled(false);
        mMenu.findItem(R.id.action_stop).setEnabled(false);
        if(FileManagement.ReadFromFile(this).size()==0){
            mMenu.findItem(R.id.action_open_file).setEnabled(false);
        }
        mMenu.findItem(R.id.action_clear_graph).setVisible(false);
        mMenu.findItem(R.id.action_open_measurement).setVisible(false);
        return true;
    }

    /**
     * Ekteleitai kathe fora pou epilegoume ena item tou menou kai analogws poio exoume epileksei ekteleitai diaforetiki diadikasia
     * @param item epistrefei to item pou exoume epileksei apo to UI.
     * @return boolean true or false.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home ) {
            bt.stop();
            mChart.quart=0f;
            if(hasDataForSave) {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .setTitle("Αποθήκευση των μετρήσεων")
                        .setMessage("Θέλετε να γίνει αποθήκευση των μετρήσεων που έχουν ληφθεί μέχρι στιγμής;")
                        .setPositiveButton("NAI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mMenu.findItem(R.id.action_open_file).setEnabled(true);
                                FileManagement.SaveToFile(MainActivity.this);
                                FileManagement.deleteAllDataTemperatures();
                                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(i);
                                hasDataForSave=false;

                                finish();
                            }

                        })
                        .setNegativeButton("ΌΧΙ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FileManagement.deleteAllDataTemperatures();
                                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                                startActivity(i);
                                hasDataForSave=false;
                                finish();
                            }

                        })
                        .show();

            }else{
                hasDataForSave=false;
                FileManagement.deleteAllDataTemperatures();
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
            return true;
        }
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
            mChart.quart=0f;
            mMenu.findItem(R.id.action_open_file).setEnabled(true);
            mMenu.findItem(R.id.action_save_file).setEnabled(false);
            FileManagement.SaveToFile(this);
            FileManagement.deleteAllDataTemperatures();
            hasDataForSave=false;
        }
        if(item.getItemId() == R.id.action_open_file){
            bt.stop();
            if(hasDataForSave) {
                new MaterialDialog.Builder(this)
                        .title("Θέλεται να γίνει αποθήκευση της μέτρησης που έχει πραγματοποιηθεί "
                                + "μέχρι στιγμής; (Αν επιλέξεται \"ΟΧΙ\" όλα τα δεδομένα θα χαθούν)")
                        .positiveText("ΝΑΙ")
                        .negativeText("ΟΧΙ")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                FileManagement.SaveToFile(MainActivity.this);
                                hasDataForSave = false;
                                FileManagement.deleteAllDataTemperatures();
                                Intent i = new Intent(MainActivity.this, OpenSaveCharts.class);
                                startActivity(i);

                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                hasDataForSave = false;
                                FileManagement.deleteAllDataTemperatures();
                                Intent i = new Intent(MainActivity.this, OpenSaveCharts.class);
                                startActivity(i);
                            }
                        })
                        .show();
            }else{
                Intent i = new Intent(MainActivity.this, OpenSaveCharts.class);
                startActivity(i);
            }
        }
        if(item.getItemId() == R.id.action_stop){
            bt.stop();
            manageButton(true);
        }
        return super.onOptionsItemSelected(item);
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
                    FileManagement.setTemp(bt.tempData, locationAddress);
                    tvAppend(tvTemperature,bt.tempData+"°C");
                    mChart.setData(chart);
                    hasDataForSave=true;
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
                    break;
                case SerialConnectionUsb.MESSAGE_READ:
                    flag= true;
                    FileManagement.setTemp(usb.tempData, locationAddress);
                    tvAppend(tvTemperature,usb.tempData+"°C");
                    mChart.setData(chart);
                    hasDataForSave=true;
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
    public void onBackPressed() {
        bt.stop();
        mChart.quart=0f;
        if(hasDataForSave) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setTitle("Αποθήκευση της μέτρησης που έχει πραγματοποιηθεί μέχρι στιγμής. (όλα τα δεδομένα θα χαθούν διαφορετικά!!)")
                    .setMessage("Θέλετε να γίνει αποθήκευση;")
                    .setPositiveButton("NAI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMenu.findItem(R.id.action_open_file).setEnabled(true);
                            FileManagement.SaveToFile(MainActivity.this);
                            Intent i = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(i);
                            hasDataForSave=false;
                            FileManagement.deleteAllDataTemperatures();
                            finish();
                        }

                    })
                    .setNegativeButton("ΌΧΙ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(i);
                            hasDataForSave=false;
                            FileManagement.deleteAllDataTemperatures();
                            finish();
                        }

                    })
                    .show();

        }else{
            hasDataForSave=false;
            FileManagement.deleteAllDataTemperatures();
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);


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

    public static void setLocation(ArrayList <String> location){
        StringBuilder sb = new StringBuilder();
        for(String pair: location){
            sb.append(pair);
        }
        locationAddress = sb.substring(0, sb.length());

    }


}
