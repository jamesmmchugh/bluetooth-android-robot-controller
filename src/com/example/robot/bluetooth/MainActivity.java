package com.example.robot.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final String TAG = "bluetooth1";

    private Button btnForward;
    private Button btnBackward;
    private Button btnLeft;
    private Button btnRight;
    private Button btnStop;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "20:14:04:11:31:91";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnForward = (Button) findViewById(R.id.btnForward);
        btnBackward = (Button) findViewById(R.id.btnBackward);
        btnLeft = (Button) findViewById(R.id.btnLeft);
        btnRight = (Button) findViewById(R.id.btnRight);
        btnStop = (Button) findViewById(R.id.btnStop);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        try {
            btSocket = createBluetoothSocket(btAdapter.getRemoteDevice(address));
            btSocket.connect();
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.e("ERROR_TAG", "ERROR_STR", e);
        }

        btnForward.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendData("f");
            }
        });

        btnBackward.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendData("b");
            }
        });

        btnLeft.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendData("l");
            }
        });

        btnRight.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendData("r");
            }
        });

        btnStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sendData("s");
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }


    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
            outStream.flush();
        } catch (IOException e) {
            Log.e("ERROR_TAG", "ERROR_STR", e);
        }
    }
}

