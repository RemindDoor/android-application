package com.example.reminddoor;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationSetup extends Application {

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    @Override
    public void onCreate(){
        super.onCreate();

        createNotifications();
    }

    public void createNotifications(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channe11 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Welcome Home",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channe11.setDescription("Please unlock your phone to open your door");

            NotificationChannel channe12 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Goodbye",
                    NotificationManager.IMPORTANCE_LOW
            );
            channe11.setDescription("Please unlock your phone to view reminders");

            NotificationManager manager = getSystemService(NotificationManager.class);

            manager.createNotificationChannel(channe11);
            manager.createNotificationChannel(channe12);
        }
    }
}
