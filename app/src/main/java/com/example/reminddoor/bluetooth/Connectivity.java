package com.example.reminddoor.bluetooth;

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

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.assist.Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;

public class Connectivity {
	static byte[] test = ("1 \t2 \t3 \t4 \t5 \t6 \t7 \t8 \t9 \t10 \t11 \t12 \t13 \t14 \t15 \t16 \t17 \t18 \t19 \t20 \t21 \t22 \t23 \t24 \t25\n" +
			"26 \t27 \t28 \t29 \t30 \t31 \t32 \t33 \t34 \t35 \t36 \t37 \t38 \t39 \t40 \t41 \t42 \t43 \t44 \t45 \t46 \t47 \t48 \t49 \t50\n" +
			"51 \t52 \t53 \t54 \t55 \t56 \t57 \t58 \t59 \t60 \t61 \t62 \t63 \t64 \t65 \t66 \t67 \t68 \t69 \t70 \t71 \t72 \t73 \t74 \t75\n" +
			"76 \t77 \t78 \t79 \t80 \t81 \t82 \t83 \t84 \t85 \t86 \t87 \t88 \t89 \t90 \t91 \t92 \t93 \t94 \t95 \t96 \t97 \t98 \t99 \t100\n" +
			"101 \t102 \t103 \t104 \t105 \t106 \t107 \t108 \t109 \t110 \t111 \t112 \t113 \t114 \t115 \t116 \t117 \t118 \t119 \t120 \t121 \t122 \t123 \t124 \t125\n" +
			"126 \t127 \t128 \t129 \t130 \t131 \t132 \t133 \t134 \t135 \t136 \t137 \t138 \t139 \t140 \t141 \t142 \t143 \t144 \t145 \t146 \t147 \t148 \t149 \t150\n" +
			"151 \t152 \t153 \t154 \t155 \t156 \t157 \t158 \t159 \t160 \t161 \t162 \t163 \t164 \t165 \t166 \t167 \t168 \t169 \t170 \t171 \t172 \t173 \t174 \t175\n" +
			"176 \t177 \t178 \t179 \t180 \t181 \t182 \t183 \t184 \t185 \t186 \t187 \t188 \t189 \t190 \t191 \t192 \t193 \t194 \t195 \t196 \t197 \t198 \t199 \t200\n").getBytes();
	
	private static BluetoothDevice bluetoothDevice = null;

	static byte[] toSend = new byte[]{};
	static int toSendPosition = 0;
	static final int SIZE = 20;
	
	public static void unlockDoor() {
		sendData("Please unlock the door.".getBytes());
	}
	
	private static byte[] buildData(byte[] inputBytes) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		
		// Write the time to prevent packet duping attacks.
		long time = System.currentTimeMillis();
		byte[] b = new byte[] {
				(byte) time,
				(byte) (time >> 8),
				(byte) (time >> 16),
				(byte) (time >> 24),
				(byte) (time >> 32),
				(byte) (time >> 40),
				(byte) (time >> 48),
				(byte) (time >> 56)};
		bytes.write(b);

		// Write the start-of-transmission marker.
		for (int i = 0; i < 4; i++) {
			bytes.write(255);
		}
		
		bytes.write(inputBytes);
		
		// Write the end-of-transmission marker.
		for (int i = 0; i < 4; i++) {
			bytes.write(255);
		}
		
		return Util.encrypt(bytes.toByteArray(), Util.getKey());
	}
	
	public static void sendData(byte[] byteArray) {
		try {
			toSend = buildData(byteArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
		toSendPosition = 0;
		
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
		if (toSendPosition >= toSend.length) return;
		
		byte[] sending = Arrays.copyOfRange(toSend, toSendPosition, Math.min(toSendPosition + SIZE, toSend.length));
		
		System.out.println("Sent!");
		
		characteristic.setValue(sending);
		gatt.writeCharacteristic(characteristic);
		toSendPosition += SIZE;
		
		if (toSendPosition >= toSend.length) {
			gatt.disconnect();
		}
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
				}
			}
			
		}
		
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
			super.onCharacteristicWrite(gatt, characteristic, status);
			
			sendNextChunk(characteristic, gatt);
		}
	};
	
}
