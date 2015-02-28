package com.example.robot.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.robot.bluetooth.bot.controller.BaseController;

public class RemoteController extends Activity {

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
				speed = (int) ((progress - CENTRE_SPEED_SLIDER) / 100d * MAX_SPEED);
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
		int leftPower = (int) (speed * (direction / 100d));
		int rightPower = (int) (speed * ((MAX_DIRECTION_SLIDER - direction) / 100d));
		baseController.setLeftAndRightPower((byte) (rightPower), (byte) (leftPower));
	}
}
