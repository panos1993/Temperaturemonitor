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
 * Is calling by MainActivity when we want to realise a connection via usb
 * It creates a connection between mobile and Arduino
 * Receives data from Arduino and sends them in MainActivity via usbHandler
 */

class SerialConnectionUsb {
    private final String ACTION_USB_PERMISSION = "com.hariharan.arduinousb.USB_PERMISSION";
    private UsbManager usbManager;//This class (usb manager) allows you to access the state of USB and communicate with USB devices. Currently only host mode is supported in the public API.
    private UsbDevice device;
    private Context mContext;
    private UsbDeviceConnection connection;
    private final Handler usbHandler;
    static final int STATE_CONNECTED = 1;
    static final int MESSAGE_READ = 3;
    static final int MESSAGE_DISCONNECTED=4;
    private UsbSerialDevice serialPort;
    String tempData;
    private StringBuilder sb = new StringBuilder();
    private UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.

        /**
         * Receiving data which are streaming via usb connection.
         * @param arg0 temperature data sending from Arduino via usb
         *
         */

        @Override
        public void onReceivedData(byte[] arg0) {

            try {
                tempData = new String(arg0, "UTF-8");       //converting bytes in text form using UTF-8 code
                sb.append(tempData);
                int endOfLineIndex = sb.indexOf("\r\n");
                if (endOfLineIndex > 0) {                               // if end-of-line
                    String sbprint = sb.substring(0, endOfLineIndex);   // extract string
                    sb.delete(0, sb.length());                          // and clear
                    Log.d("Read from usb", sbprint);
                    tempData = sbprint;
                    tempData = tempData.replaceAll("[^\\d.]", ""); //Keep only the numbers sent from the usb connection (\d mean all numbers from 0-9).
                    if(isNumeric(tempData)){
                        usbHandler.obtainMessage(MESSAGE_READ).sendToTarget();
                    }
                   // mHandler.obtainMessage(MESSAGE_READ).sendToTarget();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }
    };

    /**
     * @param str: Data which read from usb.
     * @return true if the string contains number or false if string contains other value from number.
     */
    private static boolean isNumeric(String str)
    {
        try
        {
            Double.parseDouble(str);
        }
        catch(NumberFormatException nfe) //this line run when str contains characters different from numbers
        {
            return false;
        }
        return true;
    }

    /**
     * Is the  SerialConnectionUsb class constructor.
     * Initializing and starting the connection via usb in this function.
     * This function handles the events of the connection that are happening (connect or disconnect or connection failed).
     * @param handler use this handler for refresh the status of UI in MainActivity class.
     * @param mContext Context of MainActivity class
     */
    SerialConnectionUsb(Handler handler, Context mContext) {
        this.mContext=mContext;
        usbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);//retrieving a UsbManager for access to USB devices (as a USB host) and for controlling this device's behavior as a USB device.
        usbHandler=handler;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION); //license for using usb connection
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED); // Via the UsbManager trying to detect attached USB device
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED); //Via the UsbManager trying to detect detached USB device
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ACTION_USB_PERMISSION)) { //if user allow license for using usb connection
                    boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED); //ask from user to allow the connection via usb for mobile phone and other usb device.
                    if (granted) { //if user allow the connection.
                        connection = usbManager.openDevice(device); //
                        serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                        if (serialPort != null) {
                            if (serialPort.open()) { //Set Serial Connection Parameters.
                                serialPort.setBaudRate(9600);
                                serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                serialPort.setParity(UsbSerialInterface.PARITY_NONE);//isotimia
                                serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                serialPort.read(mCallback);
                                usbHandler.obtainMessage(STATE_CONNECTED).sendToTarget(); //send message in MainActivity via handler to inform the user via message that the usb device is connected in mobile phone.

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


    /**
     * Start a connection with arduino via usb. The connection start only if device id is equal with 1027. (1027 is vendor id from my arduino this is different from device to device.
     */
    void connect() {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList(); //get a list with all devices which is connected on the mobile usb port.
        if (!usbDevices.isEmpty()) { //if the list didn't empty
            boolean deviceIsDetected = false; //while deviceIsDetected is false the repeat for run when deviceIsDetected change to true the loop for stop because we are find the device.
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) { //run all the list with detected devices. When find my device break the loop for.
                device = entry.getValue(); //device has detailed information about usb device which is connected in usb port of mobile phone
                int deviceVID = device.getVendorId();//ask from device the vendor id number.
                if (deviceVID == 1027)//check the vendor id if it is the same with 1027
                {
                    PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, new Intent(ACTION_USB_PERMISSION), 0); //ask permissions from user.
                    usbManager.requestPermission(device, pi); //check the answer of user from permissions.
                    deviceIsDetected = true;

                } else {
                    connection = null;
                    device = null;
                }

                if (deviceIsDetected)//if device is successful detective break the for loop.
                    break;//
            }
        }
    }

    /**
     * Terminate the usb connection and sends a disconnect message via a handler to the main to inform the user with a popup message about the disconnection.
     */
    void Disconnected() {
        serialPort.close();
        usbHandler.obtainMessage(MESSAGE_DISCONNECTED).sendToTarget();
    }

}
