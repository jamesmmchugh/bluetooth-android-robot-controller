package com.example.robot.bluetooth.bot.controller.socket;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Created by James on 28/02/2015.
 */
public class BumperListener implements Runnable{
    public static final int FRHIT = 0;
    public static final int FLHIT = 1;
    public static final int RRHIT = 2;
    public static final int RLHIT = 3;
    private final InputStream in;
    private final Handler handler;
    public BumperListener(InputStream inStream, Handler handler){
        this.in = inStream;
        this.handler = handler;
    }

    @Override
    public void run() {
        int input;
        try {
            while ((input = in.read()) != -1) {
                handler.dispatchMessage(Message.obtain(handler, input, new Character((char) input).toString()));
            }
        }
        catch (IOException e){
            System.err.println("Error reading from input stream");
        }
    }
}
