package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.Transition;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {
    private BluetoothAdapter adapter;
    private BluetoothService service;
    private Handler handler;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //Start the information transfer
                handler = new Handler();
                service = new BluetoothService(handler, adapter, device);
                service.start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);
        adapter = BluetoothAdapter.getDefaultAdapter();

        // TODO
        // May not need to do this (use previous connected devices)
        Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    public void onFindClassmatesClicked(View view){
        startDiscovering();

        //reading part, getting information
    }

    public void onGoBackHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startDiscovering() {

        // If we're already discovering, stop it
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        adapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (adapter != null) {
            adapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(receiver);
    }
}