package com.swift.birdsofafeather.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.swift.birdsofafeather.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ConnectedDeviceThread extends Thread {
    private final BluetoothSocket socket;
    private final InputStream inStream;
    private final OutputStream outStream;

    // Need a handler for each new thread
    private Handler handler;

    public ConnectedDeviceThread(BluetoothSocket socket, Handler handler) {
        this.socket = socket;
        InputStream tmp = null;
        OutputStream tmp2 = null;
        this.handler = handler;

        // Get the BluetoothSocket input and output streams
        try {
            tmp = this.socket.getInputStream();
            tmp2 = socket.getOutputStream();
        } catch (IOException e) {
            //Log.e(TAG, "temp sockets not created", e);
        }

        inStream = tmp;
        outStream = tmp2;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes;

        // Keep listening to the InputStream while connected
        while (true) {
            try {
                // Read from the InputStream
                bytes = inStream.read(buffer);

                Message readMsg = handler.obtainMessage(Utils.MESSAGE_READ, bytes, -1,
                        buffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                //Log.e(TAG, "disconnected", e);
                break;
            }
        }
    }

    public void write(byte[] buffer) {
        try {
            outStream.write(buffer);

            // Share the sent message back to the UI Activity
            handler.obtainMessage(Utils.MESSAGE_WRITE, -1, -1, buffer)
                    .sendToTarget();
        } catch (IOException e) {
            //Log.e(TAG, "Exception during write", e);
        }
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            //Log.e(TAG, "close() of connect socket failed", e);
        }
    }
}
