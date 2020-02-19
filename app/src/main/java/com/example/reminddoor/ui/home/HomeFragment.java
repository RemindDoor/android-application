package com.example.reminddoor.ui.home;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.AndroidException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.R;
import com.example.reminddoor.assist.FingerprintUtil;
import com.example.reminddoor.bluetooth.ArduinoCommunication;

import javax.crypto.Cipher;



public class HomeFragment extends Fragment {

	private boolean locked = true;

	private PinData myPin = new PinData();
	String iv_str;

	private Cipher cipher;
	private ImageButton lockButton;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_home, container, false);
		iv_str = myPin.readFile("my_iv.txt", getActivity());


		lockButton = root.findViewById(R.id.lockButton);
		lockButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (locked) {
					if (FingerprintUtil.supportFingerprint(getActivity())){
						FingerprintUtil.initKey();
						cipher = FingerprintUtil.initCipher();
					}
					if (cipher != null){
						authenticateFingerprint(cipher);
					}
				} else {
					lockButton.setImageResource(R.drawable.locked);
					ArduinoCommunication.closeLock();
					locked = !locked;
				}
			}
		});


		//===============================================================================
		// Set Pin
		Button set = root.findViewById(R.id.btn_set);
		set.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				if (iv_str.isEmpty()){
					SettingPinFragment setPin = new SettingPinFragment();
					setPin.show(getActivity().getSupportFragmentManager(), "SetPin");
				} else{
					PinLoginFragment pinFragment = new PinLoginFragment();
					pinFragment.show(getActivity().getSupportFragmentManager(),"EnterPin");
					pinFragment.setPinSetting(new PinLoginFragment.PinSetting() {
						@Override
						public void checkPin(boolean isSucceed) {
							if (isSucceed){
								SettingPinFragment setPin = new SettingPinFragment();
								setPin.show(getActivity().getSupportFragmentManager(), "SetPin");
							} else {
								Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
							}
						}
					});
				}

			}
		});




		//================================================================================
		// Enter_pin function
		final Button pin = root.findViewById(R.id.btn_pinLogin);
		pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	if (!locked)
					Toast.makeText(getActivity(), "The Door is already unlocked", Toast.LENGTH_SHORT).show();
            	if (!iv_str.isEmpty() && locked){
					PinLoginFragment pinFragment = new PinLoginFragment();
					pinFragment.show(getActivity().getSupportFragmentManager(),"EnterPin");
					pinFragment.setPinSetting(new PinLoginFragment.PinSetting() {
						@Override
						public void checkPin(boolean isSucceed) {
							if (isSucceed){
								Toast.makeText(getActivity(), "Succeed!", Toast.LENGTH_SHORT).show();
								ArduinoCommunication.openLock();
								lockButton.setImageResource(R.drawable.unlocked);
								locked = !locked;
							} else {
								Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
							}
						}
					});
				} else {
            		Toast.makeText(getActivity(), "Please set a Pin first", Toast.LENGTH_SHORT).show();
				}

            }
        });
		
		return root;
	}

	private void authenticateFingerprint(Cipher cipher) {
		FingerprintDialogFragment dialogFragment = new FingerprintDialogFragment();

		dialogFragment.setCipher(cipher);
		dialogFragment.show(getActivity().getFragmentManager(),"fingerprint");

		dialogFragment.setOnFingerprintSetting(new FingerprintDialogFragment.OnFingerprintSetting() {
			@Override
			public void onFingerprint(boolean isSucceed) {
				if (isSucceed){
					Toast.makeText(getActivity(), "Succeed!", Toast.LENGTH_SHORT).show();
					lockButton.setImageResource(R.drawable.unlocked);
					ArduinoCommunication.openLock();
					locked = !locked;
				} else {
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}


}