package com.example.reminddoor.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.util.Consumer;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.assist.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import javax.crypto.SecretKey;

public class Connectivity {
	private static BluetoothDevice bluetoothDevice = null;

	static byte[] toSend = new byte[]{};
	static int toSendPosition = 0;
	static final int SIZE = 20;
	static boolean done = false;
	static Consumer<byte[]> byteEater;
	
	public static byte[] buildData(byte protocol, byte[] inputBytes) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		// Write the time to prevent packet duping attacks.
		long time = System.currentTimeMillis();
		bytes.write(Util.longToBytes(time));

		// Write the start-of-transmission marker.
		for (int i = 0; i < 4; i++) {
			bytes.write(255);
		}
		
		bytes.write(protocol);
		
		bytes.write(inputBytes);
		
		// Write the end-of-transmission marker.
		for (int i = 0; i < 4; i++) {
			bytes.write(255);
		}
		
		SecretKey key = Util.getKey();
		
		if (key == null) {
			return null;
		}
		System.out.println("Encrypting with " + new String(key.getEncoded()));
		return Util.encrypt(bytes.toByteArray(), key);
	}
	
	static ProgressDialog nDialog = null;
	public static void sendData(byte[] array, Consumer<byte[]> consumer) {
		MainActivity.mainActivity.runOnUiThread(() -> {
				nDialog = new ProgressDialog(MainActivity.mainActivity);
		nDialog.setMessage("Loading..");
		nDialog.setIndeterminate(true);
		nDialog.setCancelable(true);
		nDialog.show();
		});
		
		toSend = array;
		
		if (consumer == null) {
			// Essentially do nothing.
			byteEater = bytes -> { };
		} else {
			byteEater = consumer;
		}
		
		toSendPosition = 0;
		done = false;
		
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
			connectG();
		}
	}
	
	public static void sendData(byte protocol, byte[] byteArray, Consumer<byte[]> consumer) {
		try {
			byte[] data = buildData(protocol, byteArray);
			
			if (data != null) {
				sendData(data, consumer);
			} else {
				// Kick the user back to the popup screen.
				MainActivity.kickBackToDisable.run();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private static void connectG() {
		bluetoothDevice.connectGatt(MainActivity.ctx, false, mGattCallback);
	}

	private static ScanCallback leScanCallback = new ScanCallback() {
		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			if (result.getDevice().getName() == null) return;
			System.out.println(Util.getCurrentTime() + " Found the device! " + result.getDevice().getName());
			MainActivity.BLEScanner.stopScan(leScanCallback);

			if (bluetoothDevice == null) {
				bluetoothDevice = result.getDevice();
				connectG();
			}
		}
	};
	
	private static void sendNextChunk(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
		if (toSendPosition >= toSend.length) {
			characteristic.setValue("");
			gatt.writeCharacteristic(characteristic);
			done = true;
			return;
		}
		
		byte[] sending = Arrays.copyOfRange(toSend, toSendPosition, Math.min(toSendPosition + SIZE, toSend.length));
		
		System.out.println("Sent!");
		
		characteristic.setValue(sending);
		gatt.writeCharacteristic(characteristic);
		toSendPosition += SIZE;
	}
	
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
					if (nDialog != null) {
						nDialog.dismiss();
						nDialog = null;
					}
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
						sendNextChunk(characteristic, gatt);
					}
					if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
						gatt.setCharacteristicNotification(characteristic, true);
					}
				}
			}
			
		}
		
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			if (done) {
				gatt.readCharacteristic(characteristic);
				return;
			}
			sendNextChunk(characteristic, gatt);
		}
		
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			byte[] received = characteristic.getValue();
			
			if (nDialog != null) {
				nDialog.dismiss();
				nDialog = null;
			}
			System.out.println("Received from Arduino: " + new String(received));
			System.out.println("Length: " + characteristic.getValue().length);
			
			if (new String(received).equals("The request was denied.")) {
				gatt.disconnect();
				byteEater.accept(null);
			} else if (new String(received).equals("The key is invalid.")) {
				MainActivity.kickBackToDisable.run();
				byteEater.accept(null);
			} else {
				byteEater.accept(received);
			}
			gatt.disconnect();
		}
	};
	
}
