package com.example.reminddoor;

import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class BluetoothReceiver extends BroadcastReceiver {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothDevice headphones = bluetoothAdapter.getRemoteDevice("00:00:DD:01:A5:8E");
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case BluetoothDevice.ACTION_ACL_CONNECTED: {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                System.out.println("A bluetooth device has connected!");
                break;
            }

            case BluetoothDevice.ACTION_ACL_DISCONNECTED: {
                break;
            }
        }
    }
	
	public static void addRemindersNotification(Context context){
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		
		Notification notification2 = new NotificationCompat.Builder(context, MainActivity.CHANNEL_1_ID)
				.setSmallIcon(R.mipmap.ic_launcher_round)
				.setContentTitle("RemindDoor")
				.setContentText("Your reminders: [BLANK_UNDEFINED]")
				.setStyle(new NotificationCompat.BigTextStyle()
						.bigText("Your reminders: [BLANK_UNDEFINED]"))
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setContentIntent(intent)
				.build();
		
		
		notificationManager.notify(0, notification2);
	}
    
    public static void addDoorNotification(Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
	
	    Intent notificationIntent = new Intent(context, MainActivity.class);
	    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        Notification notification2 = new NotificationCompat.Builder(context, MainActivity.CHANNEL_1_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("RemindDoor")
                .setContentText("Door Found- unlock device to open your door")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Unlock device to open your door"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
		        .setContentIntent(intent)
                .build();
        

        notificationManager.notify(0, notification2);
    }
}