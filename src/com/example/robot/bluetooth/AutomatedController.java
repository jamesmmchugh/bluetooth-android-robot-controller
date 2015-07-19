package com.example.robot.bluetooth;

import static com.example.robot.bluetooth.bot.controller.constant.BumperPin.bumperPinOf;
import static com.example.robot.bluetooth.bot.controller.constant.LoggingConstants.INFO_TAG;
import static java.lang.String.format;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;

import com.example.robot.bluetooth.bot.controller.BaseController;
import com.example.robot.bluetooth.bot.controller.InputFactory.ArdMessage;
import com.example.robot.bluetooth.bot.controller.constant.BumperPin;
import com.example.robot.bluetooth.bot.controller.socket.SerialListener;
import com.example.robot.bluetooth.bot.controller.socket.SerialListener.DebugHandler;
import com.example.robot.bluetooth.bot.controller.socket.SerialListener.MessageHandler;

public class AutomatedController extends Activity implements MessageHandler, DebugHandler, SensorEventListener {

	public static final int DEBUG_MESSAGE = 1;

	public static final int TEN_MS = 100000;

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

	private SensorManager mSensorManager;
	private Sensor mSensor;

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

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		mWindowManager = getWindow().getWindowManager();

//		mSensorManager.registerListener(this, mSensor, TEN_MS);

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
//		forward();
	}

	private void forward() {
		timer.cancel();
		timer = new Timer();
		base.sendData((byte) 0, (byte) 127, (byte) 0, (byte) 127);
	}

	private void reverse() {
		timer.cancel();
		timer = new Timer();
		base.sendData((byte) 1, (byte) 127, (byte) 1, (byte) 127);
	}

	private void stop() {
		timer.cancel();
		timer = new Timer();
		base.sendData((byte) 0, (byte) 0, (byte) 0, (byte) 0);
	}

	Timer timer = new Timer();

	private void forwardLeft() {
		timer.cancel();
		timer = new Timer();
		base.sendData((byte) 1, (byte) 127, (byte) 0, (byte) 127);
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				reverse();
//			}
//		}, 2000L);
		turnClockwise(90f);
	}

	private Float turnAngle = 0F;
	private Float turnDirection = -1F;
	private Float startAngle = null;

	private void turnClockwise(float degrees) {
		turnAngle = degrees;
		startAngle = null;
		mSensorManager.registerListener(this, mSensor, TEN_MS);
	}

	private void forwardRight() {
//		timer.cancel();
//		timer = new Timer();
//		base.sendData((byte) 0, (byte) 127, (byte) 1, (byte) 127);
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				reverse();
//			}
//		}, 2000L);
	}

	private void backLeft() {
//		timer.cancel();
//		timer = new Timer();
//		base.sendData((byte) 1, (byte) 127, (byte) 0, (byte) 127);
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				forward();
//			}
//		}, 2000L);
	}

	private void backRight() {
//		timer.cancel();
//		timer = new Timer();
//		base.sendData((byte) 1, (byte) 127, (byte) 0, (byte) 127);
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				forward();
//			}
//		}, 2000L);
	}

	@Override
	public void handleDebug(String debugMessage) {
		Log.i(INFO_TAG, debugMessage);
		handler.dispatchMessage(Message.obtain(handler, DEBUG_MESSAGE, debugMessage));
	}

	private WindowManager mWindowManager;

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			// convert the rotation-vector to a 4x4 matrix. the matrix
			// is interpreted by Open GL as the inverse of the
			// rotation-vector, which is what we want.
			updateOrientation(event.values);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	private void updateOrientation(float[] rotationVector) {
		float[] rotationMatrix = new float[9];
		SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);

		// By default, remap the axes as if the front of the
		// device screen was the instrument panel.
		int worldAxisForDeviceAxisX = SensorManager.AXIS_X;
		int worldAxisForDeviceAxisY = SensorManager.AXIS_Z;

		// Adjust the rotation matrix for the device orientation
		int screenRotation = mWindowManager.getDefaultDisplay().getRotation();
		if (screenRotation == Surface.ROTATION_0) {
			worldAxisForDeviceAxisX = SensorManager.AXIS_X;
			worldAxisForDeviceAxisY = SensorManager.AXIS_Z;
		} else if (screenRotation == Surface.ROTATION_90) {
			worldAxisForDeviceAxisX = SensorManager.AXIS_Z;
			worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_X;
		} else if (screenRotation == Surface.ROTATION_180) {
			worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_X;
			worldAxisForDeviceAxisY = SensorManager.AXIS_MINUS_Z;
		} else if (screenRotation == Surface.ROTATION_270) {
			worldAxisForDeviceAxisX = SensorManager.AXIS_MINUS_Z;
			worldAxisForDeviceAxisY = SensorManager.AXIS_X;
		}

		float[] adjustedRotationMatrix = new float[9];
		SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisForDeviceAxisX,
				worldAxisForDeviceAxisY, adjustedRotationMatrix);

		// Transform rotation matrix into azimuth/pitch/roll
		float[] orientation = new float[3];
		SensorManager.getOrientation(adjustedRotationMatrix, orientation);

		// Convert radians to degrees
		float pitch = orientation[1] * -57;
		float azimuth = (orientation[0] * -57) + 180;
		float roll = orientation[2] * -57;

		if(startAngle == null) {
			startAngle = azimuth;
		}

		final float target = (startAngle + (turnAngle * turnDirection)) % 360;

		if(turnDirection < 1) {
			if(azimuth < target) {
				mSensorManager.unregisterListener(this);
				stop();
			}
		} else {
			if(azimuth > target) {
				mSensorManager.unregisterListener(this);
				stop();
			}
		}
	}
}
