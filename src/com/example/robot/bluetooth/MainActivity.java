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
import android.widget.SeekBar;

import java.io.IOException;
import java.util.UUID;

import com.example.robot.bluetooth.bot.controller.BaseController;

public class MainActivity extends Activity {
    private static final String TAG = "bluetooth1";
    private static final int MAX_SPEED = 127;
    public static final int MAX_SPEED_SLIDER = 200;
    public static final int CENTRE_SPEED_SLIDER = 100;
    public static final int MAX_DIRECTION_SLIDER = 100;
    public static final int CENTRE_DIRECTION_SLIDER = 50;

    private Button btnStop;
    private Button btnDisconnect;
    private Button btnReconnect;
    private SeekBar sekSpeed;
    private SeekBar sekDirection;

    private BluetoothAdapter btAdapter = null;
    private BaseController baseController;

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "20:14:08:06:06:93";

    private int speed = 0;
    private int direction = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnStop = (Button) findViewById(R.id.btnStop);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnReconnect = (Button) findViewById(R.id.btnReconnect);
        sekSpeed = (SeekBar) findViewById(R.id.sekSpeed);
        sekSpeed.setMax(MAX_SPEED_SLIDER);
        sekSpeed.setProgress(CENTRE_SPEED_SLIDER);
        sekDirection = (SeekBar) findViewById(R.id.sekDirection);
        sekDirection.setMax(MAX_DIRECTION_SLIDER);
        sekDirection.setProgress(CENTRE_DIRECTION_SLIDER);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket bluetoothSocket = null;
        try {
            bluetoothSocket = createBluetoothSocket(btAdapter.getRemoteDevice(address));
        } catch (IOException e) {
            Log.e("ERROR_TAG", "ERROR_STR", e);
        }

        baseController = new BaseController(bluetoothSocket);

        btnDisconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                baseController.disconnect();
            }
        });
        btnReconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                baseController.disconnect();
                baseController.connect();
            }
        });

        btnStop.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                sekSpeed.setProgress(CENTRE_SPEED_SLIDER);
                speed = 0;
                setSpeedAndDirection(speed, direction);
            }
        });

        sekSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                speed = (int)((progress - CENTRE_SPEED_SLIDER) / 100d * MAX_SPEED);
                setSpeedAndDirection(speed, direction);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        sekDirection.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                direction = progress;
                setSpeedAndDirection(speed, direction);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }

    private void setSpeedAndDirection(int speed, int direction) {
        int leftPower = (int)(speed * (direction / 100d));
        int rightPower = (int)(speed * ((MAX_DIRECTION_SLIDER-direction) / 100d));
        baseController.setLeftAndRightPower((byte) (rightPower), (byte) (leftPower));
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return device.createRfcommSocketToServiceRecord(MY_UUID);
    }
}

