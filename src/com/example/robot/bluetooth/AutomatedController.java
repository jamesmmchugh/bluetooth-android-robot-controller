package com.example.robot.bluetooth;

import static com.example.robot.bluetooth.bot.controller.constant.BumperPin.bumperPinOf;
import static com.example.robot.bluetooth.bot.controller.constant.LoggingConstants.INFO_TAG;
import static java.lang.String.format;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.example.robot.bluetooth.bot.controller.BaseController;
import com.example.robot.bluetooth.bot.controller.InputFactory.ArdMessage;
import com.example.robot.bluetooth.bot.controller.constant.BumperPin;
import com.example.robot.bluetooth.bot.controller.socket.SerialListener;
import com.example.robot.bluetooth.bot.controller.socket.SerialListener.DebugHandler;
import com.example.robot.bluetooth.bot.controller.socket.SerialListener.MessageHandler;

public class AutomatedController extends Activity implements MessageHandler, DebugHandler {

    private BaseController base;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.automated_controller);
		base = new BaseController();
		try {
			Thread.sleep(4000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		SerialListener serialListener = new SerialListener(base.getInputStream(), this, this);
        new Thread(serialListener).start();
        start();
	}

    @Override
    public void handle(ArdMessage message) {
        switch (message.messageType) {
            case BUMPER_PRESSED: handleBumperPressed(bumperPinOf(message.payload));
                break;
            default: Log.i(INFO_TAG, format("Unhandled message of type %s", message.messageType));
        }
    }

    private void handleBumperPressed(final BumperPin bumperPin) {
        switch (bumperPin) {
            case FC: reverse();
                break;
			case BL:
			case BR: forward();
				break;
            default: Log.i(INFO_TAG, format("Unhandled bumper pin %s", bumperPin.name()));
        }
    }

    private void start() {
        forward();
    }

    private void forward() {
        base.sendData((byte)1, (byte)127, (byte)1, (byte)127);
    }

    private void reverse() {
        base.sendData((byte)0, (byte)127, (byte)0, (byte)127);
    }

    @Override
    public void handleDebug(String debugMessage) {
        Log.i(INFO_TAG, debugMessage);
    }
}