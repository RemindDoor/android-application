package com.example.reminddoor.ui.home;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.R;
import com.example.reminddoor.authentication.KeyStorage;
import com.example.reminddoor.bluetooth.ArduinoCommunication;
import com.example.reminddoor.bluetooth.Connectivity;
import com.example.reminddoor.ui.notifications.list.RemindersCalendarContainer;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class HomeFragment extends Fragment {

	private boolean locked = true;
	
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_home, container, false);
		
		final ImageButton lockButton = root.findViewById(R.id.lockButton);
		lockButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				locked = !locked;
				if (locked) {
					lockButton.setImageResource(R.drawable.locked);
					ArduinoCommunication.closeLock();
				} else {
					lockButton.setImageResource(R.drawable.unlocked);
					ArduinoCommunication.openLock();
				}
			}
		});
		
		
		return root;
	}
}