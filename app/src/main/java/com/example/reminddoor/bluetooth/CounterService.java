package com.example.reminddoor.bluetooth;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.NotificationCompat;

import com.example.reminddoor.BluetoothReceiver;
import com.example.reminddoor.MainActivity;
import com.example.reminddoor.assist.Util;

import java.util.Collections;

public class CounterService extends IntentService {
	Handler mHandler = new Handler();
	
	final Runnable longFormScans = new Runnable(){
		public void run(){
			if (Connectivity.nDialog == null) {
				connect(leScanCallback);
			}
			
			mHandler.postDelayed(longFormScans, 7000L);
		}
	};

	public static void connect(ScanCallback callback) {
		ScanFilter.Builder builder = new ScanFilter.Builder();
		builder.setDeviceAddress("A4:CF:12:8B:D5:12");
		ScanFilter filter = builder.build();
		
		ScanSettings settings = new ScanSettings.Builder()
				.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
				.setReportDelay(0)
				.build();
		System.out.println(Util.getCurrentTime() + "Started to scan");
		MainActivity.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		MainActivity.BLEScanner = MainActivity.bluetoothAdapter.getBluetoothLeScanner();
		MainActivity.BLEScanner.stopScan(callback);
		MainActivity.BLEScanner.flushPendingScanResults(callback);
		MainActivity.BLEScanner.startScan(Collections.singletonList(filter), settings, callback);
	}
	
	public static boolean notificationSend = true;
	
	private static ScanCallback leScanCallback = new ScanCallback() {
		@Override
		public void onScanResult(int callbackType, ScanResult result) {
			if (result.getDevice().getName() == null) return;
			System.out.println(Util.getCurrentTime() + " Found the device! " + result.getDevice().getName() + " " + result.getRssi());
			
			if (result.getRssi() > -60 && notificationSend) {
				notificationSend = false;
				BluetoothReceiver.addImportantNotification(MainActivity.ctx);
			} else if (result.getRssi() < -80) {
				notificationSend = true;
			}
		}
	};
	
	public CounterService() {
		super("CounterService");
		longFormScans.run();
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