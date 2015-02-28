package com.example.robot.bluetooth.bot.controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BaseController {

	// SPP UUID service
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// MAC-address of Bluetooth module (you must edit this line)
	private static String address = "20:14:08:06:06:93";

    private static final String ERROR_TAG = "ARC_ERROR";
    private static final String INFO_TAG = "ARC_INFO";
    private BluetoothSocket bluetoothSocket;
    private OutputStream outStream;
    private InputStream inputStream;

    private Byte leftPower;
    private Byte rightPower;
    public static final byte ZERO = 0;
    private static final byte ONE = 1;

    public BaseController() {
		BluetoothAdapter defaultBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		try {
			bluetoothSocket = defaultBluetoothAdapter.getRemoteDevice(address).createRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			Log.e("ERROR_TAG", "ERROR_STR", e);
		}
        this.leftPower = 0;
        this.rightPower = 0;
        connect();
    }

    public void connect() {
        try {
            if(!bluetoothSocket.isConnected()) {
                bluetoothSocket.connect();
                outStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();
            }
        } catch (IOException e) {
            Log.e(ERROR_TAG, "Failed to connect", e);
    }
}

    public void disconnect() {
        try {
            if(bluetoothSocket.isConnected()) {
                outStream.close();
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            Log.e(ERROR_TAG, "Failed to disconnect", e);
        }
    }

    public void setLeftPower(final byte power) {
        setLeftAndRightPower(power, rightPower);
    }

    public void setRightPower(final byte power) {
        setLeftAndRightPower(leftPower, power);
    }

    public void setLeftAndRightPower(final byte leftPower, final byte rightPower) {
        this.leftPower = leftPower;
        this.rightPower = rightPower;
        try {
            sendPowerLevels();
        } catch (IOException e) {
            Log.e(ERROR_TAG, "Could not set power", e);
        }
    }

    public void stop() {
        setLeftAndRightPower(ZERO, ZERO);
    }

    private void sendPowerLevels() throws IOException {
        byte leftSigned = leftPower < ZERO ? ONE : ZERO;
        byte rightSigned = rightPower < ZERO ? ONE : ZERO;
        outStream.write(new byte[] {leftSigned, (byte)Math.abs(leftPower), rightSigned, (byte)Math.abs(rightPower)});
        outStream.flush();

        String oLeftPower = String.format("%8s", Integer.toBinaryString(leftPower & 0xFF)).replace(' ', '0');;
        String oRightPower = String.format("%8s", Integer.toBinaryString(rightPower & 0xFF)).replace(' ', '0');;
        Log.i(INFO_TAG, String.format("Setting power levels L%s R%s (bits) L%s R%s", leftPower, rightPower, oLeftPower, oRightPower));
    }
}
