package com.example.robot.bluetooth.bot.controller;

import static com.example.robot.bluetooth.bot.controller.constant.LoggingConstants.ERROR_TAG;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BaseController implements Runnable {

	// SPP UUID service
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final int COMMAND_DELAY = 100;

	// MAC-address of Bluetooth module (you must edit this line)
	private static String address = "20:14:08:06:06:93";

	private BluetoothSocket bluetoothSocket;
	private OutputStream outStream;
	private InputStream inputStream;

	private Byte leftPower;
	private Byte rightPower;
	private static final byte ZERO = 0;
	private static final byte ONE = 1;

	private BlockingQueue<Command> commandList = new LinkedBlockingQueue<>(1);

	private static class Command {
		final byte leftPower;
		final byte rightPower;
		final byte leftDir;
		final byte rightDir;

		public Command(byte leftPower, byte rightPower, byte leftDir, byte rightDir) {
			this.leftPower = leftPower;
			this.rightPower = rightPower;
			this.leftDir = leftDir;
			this.rightDir = rightDir;
		}
	}

	public BaseController() {
		BluetoothAdapter defaultBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		try {
			bluetoothSocket = defaultBluetoothAdapter.getRemoteDevice(address).createRfcommSocketToServiceRecord(
					MY_UUID);
		} catch (IOException e) {
			Log.e(ERROR_TAG, "Error creating socket connection", e);
		}
		this.leftPower = 0;
		this.rightPower = 0;
		connect();
		Thread runner = new Thread(this);
		runner.start();
	}

	@Override
	public void run() {
		while(true) {
			try {
				Command command = commandList.take();
				send(command);
				Thread.sleep(COMMAND_DELAY);
			} catch (InterruptedException e) {
				Log.e(ERROR_TAG, "Base controller runner failed", e);
			}
		}
	}

	public InputStream getInputStream() {
		return this.inputStream;
	}

	public void connect() {
		try {
			if (!bluetoothSocket.isConnected()) {
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
			if (bluetoothSocket.isConnected()) {
				outStream.close();
				bluetoothSocket.close();
			}
		} catch (IOException e) {
			Log.e(ERROR_TAG, "Failed to disconnect", e);
		}
	}

	public void sendData(final byte leftDir, final byte leftPower, final byte rightDir, final byte rightPower) {
		Command command = new Command(leftPower, rightPower, leftDir, rightDir);
		commandList.poll();
        commandList.offer(command);
	}

	/*
	 * leftDirection/rightDirection : 0 = Forward, 1 = Backwards
	 * leftPower/rightPower : 0 = Stopped, 127 = Full power
	 */
	private void send(final Command command) {
		try {
			outStream.write(new byte[] { command.leftDir, command.leftPower, command.rightDir, command.rightPower });
			outStream.flush();
		} catch (IOException e) {
            Log.e(ERROR_TAG, "Could not set data", e);
		}
	}
}
