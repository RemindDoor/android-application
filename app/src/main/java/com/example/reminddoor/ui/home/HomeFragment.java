package com.example.reminddoor.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import com.an.biometric.*;
import com.example.reminddoor.MainActivity;
import com.example.reminddoor.R;
import com.example.reminddoor.bluetooth.ArduinoCommunication;



public class HomeFragment extends Fragment {

	private boolean locked = true;

	private PinData myPin = new PinData();
	String iv_str;
	private ImageButton lockButton;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_home, container, false);
		iv_str = myPin.readFile("my_iv.txt", getActivity());
		MainActivity.currentFragment = MainActivity.BottomTab.HOME;

		lockButton = root.findViewById(R.id.lockButton);
		lockButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				toggleLock();
				//TODO reenable
//				if (locked) {
//					if(BiometricUtils.isSdkVersionSupported()
//					&& BiometricUtils.isHardwareSupported(getContext()) && BiometricUtils.isFingerprintAvailable(getContext())
//					&& BiometricUtils.isPermissionGranted(getContext()))
//						authenticateFingerprint();
//					else authenticatePin();
//				} else {
//					toggleLock();
//				}
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
		
		return root;
	}

	private void authenticateFingerprint() {
		//This method will attempt to autheticate via biometrics, and will call PIN verification if this fails for any reason
		if(!locked) Toast.makeText(getActivity(), "The Door is already unlocked", Toast.LENGTH_SHORT).show();
		BiometricManager bioM = new BiometricManager.BiometricBuilder(getContext())
				.setTitle("Authentication")
				.setSubtitle("RemindDoor needs to confirm you're a registered user")
				.setDescription("Please validate via biometrics")
				.setNegativeButtonText("Try PIN Instead")
				.build();
				bioM.authenticate(biometricCallback);
	}

	BiometricCallback biometricCallback = new BiometricCallback() {
		@Override
		public void onSdkVersionNotSupported() {
			Toast.makeText(getActivity(), "SDK Version not supported", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onBiometricAuthenticationNotSupported() {
			Toast.makeText(getActivity(), "Authentication not supported", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onBiometricAuthenticationNotAvailable() {
			Toast.makeText(getActivity(), "Authentication not available", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onBiometricAuthenticationPermissionNotGranted() {
			Toast.makeText(getActivity(), "Authentication not allowed", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onBiometricAuthenticationInternalError(String error) {
			Log.e("BIOMETRICS","Internal Bio Auth Error");
		}

		@Override
		public void onAuthenticationFailed() {
		    Log.e("BIOMETRICS","Authentication doesn't match our records");
		}

		@Override
		public void onAuthenticationCancelled() {
		    Log.d("Biometrics","Authentication cancelled- user wants to use PIN");
			authenticatePin();
		}

		@Override
		public void onAuthenticationSuccessful() {
			Toast.makeText(getActivity(),"Toggling lock...", Toast.LENGTH_SHORT);
			toggleLock();
		}

		@Override
		public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
		    Log.d("Biometrics","Auth non-fatal "+helpCode+" with helpString \""+helpString+"\"");
		}

		@Override
		public void onAuthenticationError(int errorCode, CharSequence errString) {
		    Log.e("Biometrics","Auth error!");
		}
	};

	private void authenticatePin() {
		if (!locked)
			Toast.makeText(getActivity(), "The Door is already unlocked", Toast.LENGTH_SHORT).show();
		if (!iv_str.isEmpty() && locked) {
			PinLoginFragment pinFragment = new PinLoginFragment();
			pinFragment.show(getActivity().getSupportFragmentManager(), "EnterPin");
			pinFragment.setPinSetting(new PinLoginFragment.PinSetting() {
				@Override
				public void checkPin(boolean isSucceed) {
					if (isSucceed) {
						Toast.makeText(getActivity(), "Succeed!", Toast.LENGTH_SHORT).show();
						toggleLock();
					} else {
						Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
					}
				}
			});
		} else {
			Toast.makeText(getActivity(), "Please set a Pin first", Toast.LENGTH_SHORT).show();
		}
	}

	private void toggleLock(){
		if(locked){
			ArduinoCommunication.openLock();
			lockButton.setImageResource(R.drawable.unlocked);
			locked = !locked;
		}else{
			ArduinoCommunication.closeLock();
			lockButton.setImageResource(R.drawable.locked);
			locked = !locked;
		}
	}


}