package com.swift.birdsofafeather;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

public class BluetoothService {
    private ConnectDeviceThread connectThread;
    private BluetoothDevice device;
    private BluetoothAdapter adapter;
    Handler handler;

    public BluetoothService(Handler handler, BluetoothAdapter adapter, BluetoothDevice device) {
        this.handler = handler;
        this.device = device;
        this.adapter = adapter;
    }

    public synchronized void start() {
        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        connectThread = new ConnectDeviceThread(device, adapter, handler);
        connectThread.start();
    }
}
