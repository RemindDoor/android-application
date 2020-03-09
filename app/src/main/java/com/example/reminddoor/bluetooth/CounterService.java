package com.example.reminddoor.bluetooth;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
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
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.util.Consumer;

import com.example.reminddoor.BluetoothReceiver;
import com.example.reminddoor.MainActivity;
import com.example.reminddoor.R;
import com.example.reminddoor.assist.Util;

import java.io.IOException;
import java.util.Collections;

public class CounterService extends IntentService {
	Handler mHandler = new Handler();
	
	final Runnable ToastRunnable = new Runnable(){
		public void run(){
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "myapp:MyTag");
			wl.acquire(10*60*1000L /*10 minutes*/);
			
			connect();
			
			mHandler.postDelayed( ToastRunnable, 5000L);
		}
	};
	
	public static void connect() {
		ScanFilter.Builder builder = new ScanFilter.Builder();
		builder.setDeviceAddress("A4:CF:12:8B:D5:12");
		ScanFilter filter = builder.build();
		
		ScanSettings settings = new ScanSettings.Builder()
				.setScanMode(ScanSettings.SCAN_MODE_BALANCED)
				.setReportDelay(0)
				.build();
		System.out.println(Util.getCurrentTime() + "Started to scan");
		MainActivity.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		MainActivity.BLEScanner = MainActivity.bluetoothAdapter.getBluetoothLeScanner();
		MainActivity.BLEScanner.stopScan(leScanCallback);
		MainActivity.BLEScanner.flushPendingScanResults(leScanCallback);
		MainActivity.BLEScanner.startScan(Collections.singletonList(filter), settings, leScanCallback);
	}
	
	private static ScanCallback leScanCallback = new ScanCallback() {
		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			if (result.getDevice().getName() == null) return;
			System.out.println(Util.getCurrentTime() + " Found the device! " + result.getDevice().getName());
			result.getDevice().connectGatt(MainActivity.ctx, false, mGattCallback);
			
			Toast.makeText(MainActivity.ctx, "Device found: " + result.getDevice().getName() + " " + result.getRssi(), Toast.LENGTH_SHORT).show();
			
			if (result.getRssi() > -70) {
				BluetoothReceiver.addImportantNotification(MainActivity.ctx);
			}
		}
		
		@Override
		public void onScanFailed(int errorCode) {
			System.out.println("Scan failed! " + errorCode);
			super.onScanFailed(errorCode);
		}
	};
	
	private static BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
		@Override
		public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
			switch (newState) {
				case BluetoothProfile.STATE_CONNECTED:
					gatt.disconnect();
					gatt.close();
					break;
				case BluetoothProfile.STATE_DISCONNECTED:
					Log.d("BLED-GATT", "STATE_DISCONNECTED");
					gatt.close();
					break;
				default:
					Log.d("BLED-GATT", "STATE_OTHER");
			}
		}
	};
	
	public CounterService() {
		super("CounterService");
		mHandler.postDelayed( ToastRunnable, 1000);
	}
	
	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationChannel serviceChannel = new NotificationChannel(
					"ForegroundServiceChannel",
					"Foreground Service Channel",
					NotificationManager.IMPORTANCE_MIN
			);
			NotificationManager manager = getSystemService(NotificationManager.class);
			manager.createNotificationChannel(serviceChannel);
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		createNotificationChannel();
		Intent notificationIntent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this,
				0, notificationIntent, 0);
		Notification notification = new NotificationCompat.Builder(this, "ForegroundServiceChannel")
				.setContentTitle("Foreground Service")
				.setContentText("Bonjour!")
				.build();
		startForeground(1, notification);
		
		return START_REDELIVER_INTENT;
	}
	
	@Override
	protected void onHandleIntent(Intent intent) { }
}