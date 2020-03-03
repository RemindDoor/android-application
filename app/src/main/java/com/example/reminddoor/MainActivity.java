package com.example.reminddoor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		Util.popupBox(this, "Name of new user", () -> {
			String text = input.getText().toString();
			for (int i = 0; i < RemindersCalendarContainer.getSize(); i++) {
				if (text.equals(RemindersCalendarContainer.getItem(i).content)) {
					Toast.makeText(MainActivity.ctx, "This name is already taken.", Toast.LENGTH_LONG).show();
					createQRCode();
					return;
				}
			}
			
			generateQR(text);
		}, input);
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
	
	long startTime;
	long endTime;
	@RequiresApi(api = Build.VERSION_CODES.N)
	private void guestAccess() {
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		Util.popupBox(this, "Guest name", () -> {
			Util.showToast("Please choose when their stay begins.");
			showDateTimePicker(() -> {
				startTime = date.getTimeInMillis();
				Util.showToast("Please choose when their stay ends.");
				showDateTimePicker(() -> {
					endTime = date.getTimeInMillis();
					
					if (startTime > endTime) {
						Util.showToast("Their stay must start before it ends");
					} else {
						try {
							Protocol.createGuestAccount(input.getText().toString(), startTime, endTime);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			});
		}, input);
	}
	
	Calendar date;
	@RequiresApi(api = Build.VERSION_CODES.N)
	public void showDateTimePicker(Runnable runnable) {
		final Calendar currentDate = Calendar.getInstance();
		date = Calendar.getInstance();
		new DatePickerDialog(MainActivity.mainActivity, (view, year, monthOfYear, dayOfMonth) -> {
			date.set(year, monthOfYear, dayOfMonth);
			new TimePickerDialog(MainActivity.mainActivity, (view1, hourOfDay, minute) -> {
				date.set(Calendar.HOUR_OF_DAY, hourOfDay);
				date.set(Calendar.MINUTE, minute);
				runnable.run();
			}, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
		}, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
			case R.id.menu_guest_access:
				guestAccess();
				return true;
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

		if (ContextCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_BACKGROUND_LOCATION)
				!= PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
			} else {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
						2);
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
        handleIntent(getIntent());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (currentIntent != null) {
			handleIntent(currentIntent);
		}
	}
	
	Intent currentIntent = null;
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.currentIntent = intent;
    }
    
    private void handleIntent(Intent intent) {
	    Uri appLinkData = intent.getData();
        if (appLinkData != null){
            String guest_ID = appLinkData.getLastPathSegment();
	        byte[] bytes = Base64.getUrlDecoder().decode(guest_ID);
	        Util.setKey(bytes);
	        Protocol.guestUnlockSignInThingy();
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
