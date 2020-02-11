package com.example.reminddoor.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.example.reminddoor.MainActivity;

import java.io.IOException;
import java.util.UUID;

public class Connectivity {
	private static BluetoothDevice bluetoothDevice = null;
	private static final String bluetoothAddress = "98:D3:32:31:2F:A7";
	private static BluetoothSocket socket = null;
	
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
	
	public static void sendData(byte[] b) {
		ensureBTConnector();
		
		if (socket == null) {
			socket = forceBluetoothSocketConnection();
		}
		
		try {
			socket.getOutputStream().write(b);
			System.out.println("Written bytes!");
		} catch (IOException e) {
			// If we die for whatever reason, just recreate the socket again.
			socket = forceBluetoothSocketConnection();
			sendData(b);
		}
	}
}
