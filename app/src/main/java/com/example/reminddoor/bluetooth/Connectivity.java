package com.example.reminddoor.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.ui.home.HomeFragment;

import java.io.IOException;
import java.util.UUID;

public class Connectivity {
	private static BluetoothDevice bluetoothDevice = null;
	private static final String bluetoothAddress = "98:D3:32:31:2F:A7";
	
	private static void ensureBTConnector() {
		if (bluetoothDevice != null) return;
		
		for (BluetoothDevice device : MainActivity.bluetoothAdapter.getBondedDevices()) {
			if (device.getAddress().equals(bluetoothAddress)) {
				bluetoothDevice = device;
			}
		}
		
		if (bluetoothDevice == null) throw new RuntimeException("Failed to connect!");
	}
	
	private static BluetoothSocket forceBluetoothSocketConnection() {
		if (MainActivity.bluetoothAdapter.isEnabled()) {
			BluetoothDevice device = MainActivity.bluetoothAdapter.getRemoteDevice(bluetoothAddress);
			
			// The default bluetooth socket port.
			UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
			
			BluetoothSocket socket = null;
			
			try {
				socket = device.createRfcommSocketToServiceRecord(SERIAL_UUID);
			} catch (Exception e) {Log.e("","Error creating socket");}
			
			try {
				socket.connect();
				Log.e("","Connected");
				return socket;
			} catch (IOException e) {
				Log.e("",e.getMessage());
				try {
					Log.e("","trying fallback...");
					
					socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
					socket.connect();
					Log.e("","Connected");
					return socket;
				}
				catch (Exception e2) {
					Log.e("", "Couldn't establish Bluetooth connection!");
				}
			}
		}
		throw new RuntimeException("Failed to connect");
	}
	
	static byte[] toSend = new byte[]{};
	
	public static void sendData(byte[] b) {
//		ensureBTConnector();
//
//		if (socket == null) {
//			socket = forceBluetoothSocketConnection();
//		}
//
//		try {
//			socket.getOutputStream().write(b);
//			System.out.println("Written bytes!");
//		} catch (IOException e) {
//			// If we die for whatever reason, just recreate the socket again.
//			socket = forceBluetoothSocketConnection();
//			sendData(b);
//		}
		
		toSend = b;
		MainActivity.BLEScanner.startScan(leScanCallback);
	}


	private static ScanCallback leScanCallback = new ScanCallback() {
		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			if (result.getDevice().getName() == null) return;
			if (result.getDevice().getAddress().equals("A4:CF:12:8B:D5:12")) {
				MainActivity.BLEScanner.stopScan(leScanCallback);

				result.getDevice().connectGatt(MainActivity.ctx, true, mGattCallback);
			}
		}
	};
	
	private static BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			System.out.println("Connection state change!");
			
			switch (newState) {
				case BluetoothProfile.STATE_CONNECTED:
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
			System.out.println("Woo!");
			//Now we can start reading/writing characteristics
			
			for (BluetoothGattService service: gatt.getServices()) {
				System.out.println(service.getUuid());
				for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
					if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
						characteristic.setValue(toSend);
						gatt.writeCharacteristic(characteristic);
						gatt.disconnect();
					}
				}
			}
			
		}
	};
	
}
