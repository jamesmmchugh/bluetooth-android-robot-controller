package com.example.robot.bluetooth;

import static com.example.robot.bluetooth.bot.controller.constant.BumperPin.bumperPinOf;
import static com.example.robot.bluetooth.bot.controller.constant.LoggingConstants.INFO_TAG;
import static java.lang.String.format;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import com.example.robot.bluetooth.bot.controller.BaseController;
import com.example.robot.bluetooth.bot.controller.InputFactory.ArdMessage;
import com.example.robot.bluetooth.bot.controller.constant.BumperPin;
import com.example.robot.bluetooth.bot.controller.socket.SerialListener;
import com.example.robot.bluetooth.bot.controller.socket.SerialListener.DebugHandler;
import com.example.robot.bluetooth.bot.controller.socket.SerialListener.MessageHandler;

public class AutomatedController extends Activity implements MessageHandler, DebugHandler {

	public static final int DEBUG_MESSAGE = 1;

	private Button btnStop;
	private TextView text;
	private BaseController base;
	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(final Message m) {
			runOnUiThread(new Runnable() {
				public void run() {
					if (text.length() > 150) {
						text.clearComposingText();
					}
					text.append((String) m.obj);
				}
			});
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.automated_controller);

		btnStop = (Button) findViewById(R.id.btnStop);
		btnStop.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stop();
			}
		});
		text = (TextView) findViewById((R.id.serial_text));

		base = new BaseController();
		try {
			Thread.sleep(6000L);
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
			case BUMPER_PRESSED:
				handleBumperPressed(bumperPinOf(message.payload));
				break;
			default:
				Log.i(INFO_TAG, format("Unhandled message of type %s", message.messageType));
		}
	}

	private void handleBumperPressed(final BumperPin bumperPin) {
		switch (bumperPin) {
			case FC:
				reverse();
				break;
			case FL:
				backRight();
				break;
			case FR:
				backLeft();
				break;
			case BC:
				forward();
				break;
			case BL:
				forwardLeft();
				break;
			case BR:
				forwardRight();
				break;
			default:
				Log.i(INFO_TAG, format("Unhandled bumper pin %s", bumperPin.name()));
		}
	}

	private void start() {
		forward();
	}

	private void forward() {
		base.sendData((byte) 0, (byte) 127, (byte) 0, (byte) 127);
	}

	private void reverse() {
		base.sendData((byte) 1, (byte) 127, (byte) 1, (byte) 127);
	}

	private void stop() {
		base.sendData((byte) 0, (byte) 0, (byte) 0, (byte) 0);
	}

	Timer timer = new Timer();

	private void forwardLeft() {
		base.sendData((byte) 0, (byte) 80, (byte) 0, (byte) 127);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				reverse();
			}
		}, 2000L);
	}

	private void forwardRight() {
		base.sendData((byte) 0, (byte) 127, (byte) 0, (byte) 80);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				reverse();
			}
		}, 2000L);
	}

	private void backLeft() {
		base.sendData((byte) 0, (byte) 80, (byte) 0, (byte) 127);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				forward();
			}
		}, 2000L);
	}

	private void backRight() {
		base.sendData((byte) 1, (byte) 127, (byte) 1, (byte) 80);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				forward();
			}
		}, 2000L);
	}

	@Override
	public void handleDebug(String debugMessage) {
		Log.i(INFO_TAG, debugMessage);
		handler.dispatchMessage(Message.obtain(handler, DEBUG_MESSAGE, debugMessage));
	}
}
