package com.example.robot.bluetooth.bot.controller.socket;

import java.io.IOException;
import java.io.InputStream;
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
    private final Queue<Integer> bumperQueue;
    public BumperListener(InputStream inStream, Queue<Integer> bumperQueue){
        this.in = inStream;
        this.bumperQueue = bumperQueue;
    }

    @Override
    public void run() {
        int input;
        try {
            while ((input = in.read()) != -1) {
                bumperQueue.add(input);
            }
        }
        catch (IOException e){
            System.err.println("Error reading from input stream");
        }
    }
}
