package com.example.reminddoor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BluetoothReceiver extends BroadcastReceiver {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice headphones = bluetoothAdapter.getRemoteDevice("00:00:DD:01:A5:8E");
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED: {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getAddress().equals(headphones.getAddress())) {
                    //Do what you want
                    System.out.println("Connected to bluetooth device");
                }
                break;
            }

            case BluetoothDevice.ACTION_ACL_DISCONNECTED: {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                break;
            }
        }
    }
}
