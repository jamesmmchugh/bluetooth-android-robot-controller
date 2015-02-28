package com.example.robot.bluetooth;

import android.app.Activity;
import android.os.Bundle;
import com.example.robot.bluetooth.bot.controller.BaseController;
import com.example.robot.bluetooth.bot.controller.socket.BumperListener;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class AutomatedController extends Activity {
    private final BaseController base;
    private final LinkedBlockingQueue<Integer> bumperQueue;
    private byte leftPower = 0;
    private byte rightPower = 0;
    public AutomatedController(){
        this.base = new BaseController();
        this.bumperQueue = new LinkedBlockingQueue<Integer>(2);
    }

    private void updateBase(){
        base.setLeftAndRightPower(leftPower, rightPower);
    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.automated_controller);
        leftPower = 50;
        rightPower = 50;
        updateBase();
        Thread bumperListener = new Thread(new BumperListener(base.getInputStream(), bumperQueue));
        Integer bump;
        try {
            while ((bump = bumperQueue.take()) != null) {

            }
        }
        catch(InterruptedException e){
            System.err.println("Queue done fucked up");
        }
	}

    private void backup(Integer bump){
        leftPower = (byte)(0 - leftPower);
        rightPower = (byte)(0 - rightPower);
        switch(bump){
            case BumperListener.FRHIT:
                rightPower = (byte)(rightPower - 20);
                break;
            case BumperListener.FLHIT:
                leftPower = (byte)(leftPower - 20);
                break;
            case BumperListener.RRHIT:
                rightPower = (byte)(rightPower + 20);
                break;
            case BumperListener.RLHIT:
                leftPower = (byte)(leftPower + 20);
                break;
        }
        updateBase();
        try {
            Thread.sleep(2000);
            rightPower = 50;
            leftPower = 50;
            updateBase();
        }
        catch (InterruptedException e){
            System.err.println("Some asswipe interrupted the wait");
        }
    }
}