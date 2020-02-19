package com.example.reminddoor;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.reminddoor.ui.notifications.list.RemindersCalendarContainer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_ENABLE_BT = 1;

	public static BluetoothAdapter bluetoothAdapter = null;
	public static BluetoothLeScanner BLEScanner = null;
	
	public static Context ctx = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RemindersCalendarContainer.load(getDir("data", MODE_PRIVATE));
		
		setContentView(R.layout.activity_main);
		BottomNavigationView navView = findViewById(R.id.nav_view);
		// Passing each menu ID as a set of Ids because each
		// menu should be considered as top level destinations.
		AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
				R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
				.build();
		NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
		NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
		NavigationUI.setupWithNavController(navView, navController);
		
		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {
			
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {
			} else {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						1);
			}
		}
		
		ctx = this.getApplicationContext();
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BLEScanner = bluetoothAdapter.getBluetoothLeScanner();
		if (bluetoothAdapter == null) {
			System.out.println("This device does not support bluetooth!");
		} else {
			if (!bluetoothAdapter.isEnabled()) {
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

				IntentFilter intentFilter = new IntentFilter();
				intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
				intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		RemindersCalendarContainer.save(getDir("data", MODE_PRIVATE));
	}
}
