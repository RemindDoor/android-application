package com.example.reminddoor.ui.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.R;
import com.example.reminddoor.ui.barcode.QRCodeUtil;
import com.example.reminddoor.ui.notifications.list.RemindersCalendarContainer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DisabledFragment extends DialogFragment {
	public static Runnable dismiss = null;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Light_Dialog);
		
		dismiss = this::dismiss;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_disabled, container);
		AppCompatButton scanQRButton = v.findViewById(R.id.scan_qr_code);
		scanQRButton.setOnClickListener(t -> {
			((MainActivity) getContext()).scan();
		});
		return v;
	}
}