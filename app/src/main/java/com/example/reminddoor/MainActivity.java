package com.example.reminddoor;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.reminddoor.assist.Util;
import com.example.reminddoor.bluetooth.Connectivity;
import com.example.reminddoor.bluetooth.Protocol;
import com.example.reminddoor.ui.barcode.showQRCode_fragment;
import com.example.reminddoor.ui.home.DisabledFragment;
import com.example.reminddoor.ui.notifications.list.RemindersCalendarContainer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_ENABLE_BT = 1;

	public static BluetoothAdapter bluetoothAdapter = null;
	public static BluetoothLeScanner BLEScanner = null;
	
	public static Context ctx = null;
	public static MainActivity mainActivity;
	
	public static BottomTab currentFragment = BottomTab.HOME;
	
	public enum BottomTab {
		HOME, DOOR, REMINDERS
	}
	
	// Menu part
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.top_menu, menu);
		return true;
	}

	public void createQRCode() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Name of new user");

		// Set up the input
		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		builder.setView(input);

		// Set up the buttons
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String text = input.getText().toString();
				for (int i = 0; i < RemindersCalendarContainer.getSize(); i++) {
					if (text.equals(RemindersCalendarContainer.getItem(i).content)) {
						Toast.makeText(MainActivity.ctx, "This name is already taken.", Toast.LENGTH_LONG).show();
						createQRCode();
						return;
					}
				}
				
				generateQR(text);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		builder.show();
	}
	
	private void generateQR(String name) {
		try {
			byte[] paddedName = Util.padRight(name, 31).getBytes();
			byte[] bytes = Connectivity.buildData(Protocol.Type.NEW_USER.get(), paddedName);
			String toSend = new String(bytes, StandardCharsets.ISO_8859_1);
			showQRCode_fragment show_fragment = new showQRCode_fragment(toSend);
			show_fragment.show(this.getSupportFragmentManager(), "QR_Code");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menu_scan:
				scan();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public static Runnable kickBackToDisable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainActivity = this;
		ctx = this.getApplicationContext();
		RemindersCalendarContainer.load(getDir("data", MODE_PRIVATE));
		
		kickBackToDisable = () -> {
			DisabledFragment show_fragment = new DisabledFragment();
			show_fragment.show(mainActivity.getSupportFragmentManager(), "Disabled.");
		};
		
		if (Util.getKey() == null) {
			kickBackToDisable.run();
		}
		
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
		
//		Util.getKey();
		
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

	public void scan(){
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE); // Barcode type: only QR_code
		integrator.setPrompt("Scan a barcode");
		integrator.setOrientationLocked(true); // lock orientation
		integrator.setCameraId(0);  // Use a specific camera of the device, 0 - back camera, 1 - front
		integrator.setBeepEnabled(false); // beep On/Off
		integrator.setBarcodeImageEnabled(false); // save Barcode on local
		integrator.initiateScan();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (result != null) {
			if (result.getContents() == null) {
				String msg = "Cancelled";
				Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
				
			} else {
				// This should then just fire straight across bluetooth.
				
				String barcodeContents = result.getContents();
				byte[] toSend = barcodeContents.getBytes(StandardCharsets.ISO_8859_1);
				Protocol.addUser(toSend);
			}
		}
	}
}
