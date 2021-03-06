package com.example.robot.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.robot.bluetooth.bot.controller.BaseController;

public class RemoteController extends Activity {

	public static final int MAX_SPEED_SLIDER = 200;
	public static final int CENTRE_SPEED_SLIDER = 100;
	public static final int MAX_DIRECTION_SLIDER = 100;
	public static final int CENTRE_DIRECTION_SLIDER = 50;

	private Button btnStop;
	private Button btnDisconnect;
	private Button btnReconnect;
	private SeekBar sekSpeed;
	private SeekBar sekDirection;

	private BaseController baseController;

	private int speed = 0;
	private int direction = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote_controller);

		btnStop = (Button) findViewById(R.id.btnStop);
		btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
		btnReconnect = (Button) findViewById(R.id.btnReconnect);
		sekSpeed = (SeekBar) findViewById(R.id.sekSpeed);
		sekSpeed.setMax(MAX_SPEED_SLIDER);
		sekSpeed.setProgress(CENTRE_SPEED_SLIDER);
		sekDirection = (SeekBar) findViewById(R.id.sekDirection);
		sekDirection.setMax(MAX_DIRECTION_SLIDER);
		sekDirection.setProgress(CENTRE_DIRECTION_SLIDER);

		baseController = new BaseController();

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
				speed = progress - CENTRE_SPEED_SLIDER;
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

	private static final String INFO_TAG = "ARC_INFO";

	private void setSpeedAndDirection(int speed, int direction) {
		//speed is -100 to 100, 0 = stop
		//direction is 0 to 100, 50 = both
		byte forward = 1;
		byte backward = 0;
		byte dir = speed < 0 ? forward : backward;
		int leftPower;
		int rightPower;
		if(direction == 50) { // full forward
			leftPower = (int) (127 * (Math.abs(speed) / 100d));
			rightPower = (int) (127 * (Math.abs(speed) / 100d));
		} else if(direction > 50) { //turning right
			leftPower = (int) (127 * (Math.abs(speed)/100d) * (direction / 50d));
			rightPower = (int) (127 * (Math.abs(speed)/100d));
		} else { //turning left
			leftPower = (int) (127 * (Math.abs(speed) / 100d));
			rightPower = (int) (127 * (Math.abs(speed) / 100d) * ((MAX_DIRECTION_SLIDER - direction) / 50d));
		}
		Log.i(INFO_TAG, String.format("Sending power levels L%s R%s from speed %s direction %s", leftPower, rightPower, speed, direction));
		baseController.sendData(dir, (byte) leftPower, dir, (byte) rightPower);


//
//
//
//
//		int leftPower;
//		int rightPower;
//		if(direction == 50) { // full forward
//			leftPower = (int) (255 * (speed / 100d));
//			rightPower = (int) (255 * (speed / 100d));
//		} else if(direction > 50) { //turning right
//			leftPower = (int) (255 * (speed / 100d));
//			rightPower = (int) (255 * (speed / 100d) * ((MAX_DIRECTION_SLIDER - direction) / 50d));
//		} else { //turning left
//			leftPower = (int) (255 * (speed/100d) * (direction / 50d));
//			rightPower = (int) (255 * (speed/100d));
//		}
//		Log.i(INFO_TAG, String.format("Sending power levels L%s R%s", leftPower, rightPower));
//		baseController.setLeftAndRightPower((byte) (rightPower), (byte) (leftPower));
	}
}
