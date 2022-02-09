package com.swift.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class BluetoothCheckActivity extends AppCompatActivity{
    private static final int REQUEST_BT_CONNECT = 1;
    private static final int REQUEST_BT_SCAN = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private boolean ALLOW_BT_CONNECT = false;
    private boolean ALLOW_BT_SCAN = false;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_check);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Utils.showAlert(this, "Device doesn't have bluetooth capability, exiting app");
            finish();
        }

        checkPermissions();

        if (!bluetoothAdapter.isEnabled() && ALLOW_BT_CONNECT) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Intent nameIntent = new Intent(this, NameActivity.class);
        startActivity(nameIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length == 0)
            return;

        switch(requestCode) {
            case 1:
                if(permissions[0].equals(Manifest.permission.BLUETOOTH_CONNECT) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ALLOW_BT_CONNECT = true;
                else
                    Utils.showAlert(this, "Bluetooth Connect permissions will be required to connect to other devices as well as turn on Bluetooth. " +
                            "This isn't needed right now, but we will ask again later to ensure app functionality.");
                return;
            case 2:
                if(permissions[0].equals(Manifest.permission.BLUETOOTH_SCAN) && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ALLOW_BT_SCAN = true;
                else
                    Utils.showAlert(this, "Bluetooth scan permissions will be required to scan for nearby bluetooth devices. " +
                            "This isn't needed right now, but we will ask again later to ensure app functionality.");
                return;
        }
    }

    protected void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BT_CONNECT);
        else
            ALLOW_BT_CONNECT = true;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BT_SCAN);
        else
            ALLOW_BT_SCAN = true;
    }
}