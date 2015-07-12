package com.example.robot.bluetooth.bot.controller.socket;

import static com.example.robot.bluetooth.bot.controller.constant.BumperPin.bumperPinOf;
import static com.example.robot.bluetooth.bot.controller.constant.LoggingConstants.ERROR_TAG;
import static com.example.robot.bluetooth.bot.controller.constant.LoggingConstants.INFO_TAG;
import static com.example.robot.bluetooth.bot.controller.constant.MessageType.BUMPER_PRESSED;
import static java.lang.String.format;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import com.example.robot.bluetooth.bot.controller.InputFactory;
import com.example.robot.bluetooth.bot.controller.InputFactory.ArdMessage;
import com.example.robot.bluetooth.bot.controller.constant.BumperPin;
import com.example.robot.bluetooth.bot.controller.constant.MessageType;

public class SerialListener implements Runnable {

	private static final int STREAM_DISCONNECTED_CNST = -1;
	private static final int DEBUG_MSG_STOP_CNST = 42; // ASCII equivalent: *

	private final InputStream in;
	private final InputFactory inputFactory = new InputFactory();
	private final DebugHandler debugHandler;
	private final MessageHandler messageHandler;

	public SerialListener(InputStream inStream, DebugHandler debugHandler, MessageHandler messageHandler) {
		this.in = inStream;
		this.debugHandler = debugHandler;
		this.messageHandler = messageHandler;
	}

	@Override
	public void run() {
		int input;
		try {
			while ((input = in.read()) != STREAM_DISCONNECTED_CNST) {
				ArdMessage message = inputFactory.readMessage(input);
				if (message.messageType == MessageType.DEBUG_MESSAGE) {
					readDebugUntilStopCharacter();
				} else {
					serviceMessage(message);
				}
			}
		} catch (IOException e) {
			Log.e(ERROR_TAG, "Error reading from input stream", e);
		}
	}

	private void readDebugUntilStopCharacter() throws IOException {
		int input;
		StringBuilder message = new StringBuilder();
		while ((input = in.read()) != DEBUG_MSG_STOP_CNST) {
			message.append(new Character((char) input).toString());
		}
		debugHandler.handleDebug(message.toString());
	}

	private void serviceMessage(ArdMessage message) {
		messageHandler.handle(message);
		if (message.messageType == BUMPER_PRESSED) {
			BumperPin bumperPin = bumperPinOf(message.payload);
			Log.i(INFO_TAG, format("%s Bumper pressed", bumperPin.name()));
		}
	}

	public interface MessageHandler {
		void handle(ArdMessage message);
	}

	public interface DebugHandler {
		void handleDebug(String debugMessage);
	}
}
