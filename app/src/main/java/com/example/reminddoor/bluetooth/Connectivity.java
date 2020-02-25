package com.example.reminddoor.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.assist.Util;
import com.example.reminddoor.ui.home.HomeFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Connectivity {
	private static BluetoothDevice bluetoothDevice = null;

	static byte[] toSend = new byte[]{};
	
	public static void sendData(byte[] b) {
		toSend = b;
		ScanFilter.Builder builder = new ScanFilter.Builder();
		builder.setDeviceAddress("A4:CF:12:8B:D5:12");
		ScanFilter filter = builder.build();

		ScanSettings settings = new ScanSettings.Builder()
				.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
				.setReportDelay(0)
				.build();

		System.out.println(Util.getCurrentTime() + "Started to scan");
		if (bluetoothDevice == null) {
			MainActivity.BLEScanner.startScan(Collections.singletonList(filter), settings, leScanCallback);
		} else {
			bluetoothDevice.connectGatt(MainActivity.ctx, false, mGattCallback);
		}
	}


	private static ScanCallback leScanCallback = new ScanCallback() {
		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			if (result.getDevice().getName() == null) return;
			System.out.println(Util.getCurrentTime() + " Found the device! " + result.getDevice().getName());
			MainActivity.BLEScanner.stopScan(leScanCallback);

			if (bluetoothDevice == null) {
				bluetoothDevice = result.getDevice();
				bluetoothDevice.connectGatt(MainActivity.ctx, false, mGattCallback);
			}
		}
	};
	
	private static BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

			switch (newState) {
				case BluetoothProfile.STATE_CONNECTED:
					System.out.println(Util.getCurrentTime() + "Connected!");
					Log.d("BLED-GATT", "STATE_CONNECTED");
					gatt.discoverServices();
					break;
				case BluetoothProfile.STATE_DISCONNECTED:
					Log.d("BLED-GATT", "STATE_DISCONNECTED");
					gatt.close();
					break;
				default:
					Log.d("BLED-GATT", "STATE_OTHER");
			}
		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			for (BluetoothGattService service: gatt.getServices()) {
				for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
					if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
						characteristic.setValue(toSend);
						gatt.writeCharacteristic(characteristic);
						System.out.println(Util.getCurrentTime() + "Sent data finally!");
						gatt.disconnect();
					}
				}
			}
			
		}
	};
	
}
