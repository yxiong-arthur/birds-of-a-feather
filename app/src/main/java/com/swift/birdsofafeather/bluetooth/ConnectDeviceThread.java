package com.swift.birdsofafeather.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

public class ConnectDeviceThread extends Thread {
    private final BluetoothSocket socket;
    private final BluetoothDevice device;
    private ConnectedDeviceThread connectedThread;
    private BluetoothAdapter adapter;
    private String appName = "BOF";
    private UUID myUUID;

    // Need a handler for each new thread
    private Handler handler;

    public ConnectDeviceThread(BluetoothDevice device, BluetoothAdapter adapter, Handler handler) {
        this.device = device;
        this.adapter = adapter;
        this.handler = handler;
        myUUID = UUID.randomUUID();
        BluetoothSocket tmp = null;

        try {
            tmp = this.device.createRfcommSocketToServiceRecord(myUUID);
        } catch (IOException e2) {
            //Log
        }
        socket = tmp;
    }

    public void run() {
        adapter.cancelDiscovery();

        // Make a connection to the BluetoothSocket
        try {
            socket.connect();
        } catch (IOException e) {
            // Close the socket
            try {
                socket.close();
            } catch (IOException e2) {
                //Log.e(TAG, "unable to close() " + mSocketType +
                //         " socket during connection failure", e2);
            }
            return;
        }

        // Start the connected thread
        connected(socket);
    }

    private synchronized void connected(BluetoothSocket socket) {
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectedThread = new ConnectedDeviceThread(socket, handler);
        connectedThread.start();
    }

    public void cancel() {
        try {
            socket.close();
        } catch (IOException e) {
            //Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
        }
    }
}
