package com.example.gioti.temperaturemonitor;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gioti on 13/2/2018.d
 */

class SerialConnectionUsb {
    private final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    private UsbManager usbManager;
    private UsbDevice device;
    private Context mContext;
    private UsbDeviceConnection connection;
    private final Handler mHandler;
    static final int STATE_CONNECTED = 1;
    static final int MESSAGE_DEVICE_NAME = 2;
    static final int MESSAGE_READ = 3;
    static final int MESSAGE_DISCONNECTED=4;
    private UsbSerialDevice serialPort;
    String tempData;
    private StringBuilder sb = new StringBuilder();
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {
            try {
                tempData = new String(arg0, "UTF-8");
                sb.append(tempData);
                int endOfLineIndex = sb.indexOf("\r\n");
                if (endOfLineIndex > 0) {                                            // if end-of-line,
                    String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                    sb.delete(0, sb.length());                                      // and clear
                    Log.d("Read from bluetooth", sbprint);
                    tempData = sbprint;
                    tempData = tempData.replaceAll("[^\\d.]", ""); //Keep only the numbers sent from the usb connection (\d mean all numbers from 0-9).
                    if(isNumeric(tempData)){
                        mHandler.obtainMessage(MESSAGE_READ).sendToTarget();
                    }
                   // mHandler.obtainMessage(MESSAGE_READ).sendToTarget();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
    };

    private static boolean isNumeric(String str)
    {
        try
        {
            Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
    SerialConnectionUsb(Handler handler, Context mContext) {
        this.mContext=mContext;
        usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        mHandler=handler;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if (granted) {
                        connection = usbManager.openDevice(device);
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) { //Set Serial Connection Parameters.
                                serialPort.setBaudRate(9600);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(mCallback);

                                //tvAppend(textView, "Serial Connection Opened!\n");

                            } else {
                                Log.d("SERIAL", "PORT NOT OPEN");
                            }
                        } else {
                            Log.d("SERIAL", "PORT IS NULL");
                        }
                    } else {
                        Log.d("SERIAL", "PERM NOT GRANTED");
                    }
                } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    Disconnected();
                }

            }
        };
        mContext.registerReceiver(broadcastReceiver, filter);
    }


    void connect() {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 1027)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                    mHandler.obtainMessage(STATE_CONNECTED).sendToTarget();
                    mHandler.obtainMessage(MESSAGE_DEVICE_NAME,device.getDeviceName()).sendToTarget();
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }
    }
    private void Disconnected() {
        serialPort.close();
        mHandler.obtainMessage(MESSAGE_DISCONNECTED).sendToTarget();
    }

}
