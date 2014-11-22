package com.example.robot.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

public class BaseController {

    private static final String ERROR_TAG = "ARC_ERROR";
    private static final String INFO_TAG = "ARC_INFO";
    private BluetoothSocket bluetoothSocket;
    private OutputStream outStream;

    private byte leftPower;
    private byte rightPower;

    public BaseController(final BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
        this.leftPower = 0;
        this.rightPower = 0;
        connect();
    }

    public void connect() {
        try {
            if(!bluetoothSocket.isConnected()) {
                bluetoothSocket.connect();
                outStream = bluetoothSocket.getOutputStream();
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
        byte zero = 0;
        setLeftAndRightPower(zero, zero);
    }

    private void sendPowerLevels() throws IOException {
        Log.i(INFO_TAG, String.format("Setting power levels L%s R%s", leftPower, rightPower));
        outStream.write(new byte[] {leftPower, rightPower});
        outStream.flush();
    }
}
