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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.folderselector.FileChooserDialog;
import com.afollestad.materialdialogs.folderselector.FolderChooserDialog;
import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    static ManageChart mChart = new ManageChart();
    private static boolean flag=true,hasDataForSave=false;
    static CharSequence[] selectedMesurments;
    static CharSequence addressloc,year1,month1,date1;
    static ArrayList<SaveModel> measurments = new ArrayList<>();
    private static String locationAdress;
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
        measurments.clear();


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
            //mChart.resetGraph(chart);
            usb.connect();
        }
        if(item.getItemId() == R.id.action_bluetooth){
           // mChart.resetGraph(chart);
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
            ArrayList<String> address = new ArrayList<>();
            for(SaveModel pair: fm.ReadFromFile(this)){
                address.add(pair.getLocation());
            }
           Set<String> hs = new HashSet<>();
            hs.addAll(address);
            address.clear();
            address.addAll(hs);
            new MaterialDialog.Builder(this)
                    .title("Επιλέξτε την τοποθεσία που πραγματοποιήθηκε η μέτρηση θερμοκρασίας!!!")
                    .items(address)
                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                            addressloc=text;
                            final ArrayList<String> year = new ArrayList<>();
                            for(SaveModel pair: fm.ReadFromFile(MainActivity.this)){
                                    if(pair.getLocation().equals(addressloc.toString())){
                                        year.add(pair.getYear());
                                    }
                            }

                            Set<String> hs = new HashSet<>();
                            hs.addAll(year);
                            year.clear();
                            year.addAll(hs);

                            new MaterialDialog.Builder(MainActivity.this)
                                    .title("Επιλέξτε το έτος που πραγματοποιήθηκε η μέτρηση!!!")
                                    .items(year)
                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                            year1=text;
                                            ArrayList<String> month = new ArrayList<>();
                                            for(SaveModel pair: fm.ReadFromFile(MainActivity.this)){
                                                if((pair.getLocation().equals(addressloc.toString()) && (pair.getYear().equals(year1.toString())))){
                                                    month.add(pair.getMonth());
                                                }
                                            }

                                            Set<String> hs = new HashSet<>();
                                            hs.addAll(month);
                                            month.clear();
                                            month.addAll(hs);
                                            new MaterialDialog.Builder(MainActivity.this)
                                                    .title("Επιλέξτε το μήνα που πραγματοποιήθηκε η μέτρηση")
                                                    .items(month)
                                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                                        @Override
                                                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                                            month1=text;
                                                            ArrayList<String> date = new ArrayList<>();
                                                            for(SaveModel pair: fm.ReadFromFile(MainActivity.this)){
                                                                if((pair.getLocation().equals(addressloc.toString()) && (pair.getYear().equals(year1.toString()))) && (pair.getMonth().equals(month1.toString()))){
                                                                    date.add(pair.getDate());
                                                                }
                                                            }

                                                            Set<String> hs = new HashSet<>();
                                                            hs.addAll(date);
                                                            date.clear();
                                                            date.addAll(hs);
                                                            new MaterialDialog.Builder(MainActivity.this)
                                                                    .title("Επιλέξτε την ημέρα του μήνα " + month1.toString()+ " που πραγματοποιήθηκε η μέτρηση")
                                                                    .items(date)
                                                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                                                        @Override
                                                                        public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                                                            date1=text;
                                                                            ArrayList<String> seconds = new ArrayList<>();
                                                                            for(SaveModel pair: fm.ReadFromFile(MainActivity.this)){
                                                                                if((pair.getLocation().equals(addressloc.toString()) && (pair.getYear().equals(year1.toString()))) && (pair.getMonth().equals(month1.toString())) && (pair.getDate().equals(date1.toString()))){
                                                                                    seconds.add(pair.getSeconds());
                                                                                }
                                                                            }


                                                                            new MaterialDialog.Builder(MainActivity.this)
                                                                                    .title("Επιλέξτε τις μετρήσεις που θέλεται να εμφανίσεται για την ημέρα " + date1.toString() + " " + month1.toString() + "του έτους " + year1.toString() )
                                                                                    .items(seconds)
                                                                                    .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                                                                                        @Override
                                                                                        public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                                                                            selectedMesurments=text;
                                                                                            for(int i=0; i<selectedMesurments.length; i++){
                                                                                                Log.d("TAG", selectedMesurments[i].toString());
                                                                                            }
                                                                                            for(SaveModel pair: fm.ReadFromFile(MainActivity.this)){
                                                                                                if((pair.getLocation().equals(addressloc.toString()) && (pair.getYear().equals(year1.toString()))) && (pair.getMonth().equals(month1.toString())) && (pair.getDate().equals(date1.toString()))){
                                                                                                    for(CharSequence pair2: selectedMesurments){
                                                                                                        if(pair.getSeconds().equals(pair2.toString())){
                                                                                                            measurments.add(pair);
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            final Intent i = new Intent(MainActivity.this, OpenSaveCharts.class);
                                                                                            if(selectedMesurments.length>0) {
                                                                                                startActivity(i);
                                                                                                finish();
                                                                                            }
                                                                                            return true;
                                                                                        }
                                                                                    })
                                                                                    .positiveText(android.R.string.ok)
                                                                                    .negativeText( android.R.string.cancel )
                                                                                    .show();
                                                                            return  true;
                                                                        }
                                                                    })
                                                                    .positiveText(android.R.string.ok)
                                                                    .negativeText( android.R.string.cancel )
                                                                    .show();
                                                        return  true;
                                                        }
                                                    })
                                                    .positiveText(android.R.string.ok)
                                                    .negativeText( android.R.string.cancel )
                                                    .show();

                                            return true;
                                        }
                                    })
                                    .positiveText(android.R.string.ok)
                                    .negativeText( android.R.string.cancel )
                                    .show();


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


        // ...

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
                    fm.setTemp(bt.tempData,locationAdress);
                    tvAppend(tvTemperature,bt.tempData+"°C");
                    mChart.setData(chart,fm);
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

                    //Log.d(TAG, "MESSAGE_STATE_CHANGE: " + message.arg1);
                    break;
                case SerialConnectionUsb.MESSAGE_READ:
                    flag= true;
                    //Float temperature = Float.valueOf(usb.tempData);
                    fm.setTemp(usb.tempData,locationAdress);
                    mChart.setData(chart,fm);
                    //tvAppend(tvinformation,"Data transfered " + usb.tempData);
                    tvAppend(tvTemperature,usb.tempData + "°C");
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
                    .setMessage("Θέλεται να γίνει αποθήκευση;")
                    .setPositiveButton("NAI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMenu.findItem(R.id.action_open_file).setEnabled(true);
                            fm.SaveToFile(MainActivity.this);
                            Intent i = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(i);
                            hasDataForSave=false;
                            fm.deleteAllDataTemperatures();
                            finish();
                        }

                    })
                    .setNegativeButton("ΌΧΙ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(i);
                            hasDataForSave=false;
                            fm.deleteAllDataTemperatures();
                            finish();
                        }

                    })
                    .show();

        }else{
            hasDataForSave=false;
            fm.deleteAllDataTemperatures();
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
        locationAdress=sb.substring(0, sb.length());

    }

}
