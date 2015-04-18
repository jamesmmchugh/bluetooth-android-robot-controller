package com.example.robot.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btnRemoteController;
	private Button btnAutomatedController;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Context context = this;

		btnRemoteController = (Button) findViewById(R.id.btnRemoteController);
		btnAutomatedController = (Button) findViewById(R.id.btnAutomatedController);

		btnRemoteController.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent remoteControllerIntent = new Intent(context, RemoteController.class);
				startActivity(remoteControllerIntent);
			}
		});

		btnAutomatedController.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent remoteControllerIntent = new Intent(context, AutomatedController.class);
				startActivity(remoteControllerIntent);
			}
		});
	}
}

