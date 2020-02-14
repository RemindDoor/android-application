package com.example.reminddoor.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import com.example.reminddoor.R;
import com.example.reminddoor.assist.FingerprintUtil;

import javax.crypto.Cipher;



public class HomeFragment extends Fragment {

	private boolean locked = true;

	private Cipher cipher;
	private ImageButton lockButton;

	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_home, container, false);

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
					locked = !locked;
				}
			}
		});

		// Set pin function
		final Button pin = root.findViewById(R.id.btn_pinLogin);
		pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				PinLoginFragment pinFragment = new PinLoginFragment();
				pinFragment.show(getActivity().getSupportFragmentManager(),"xxx");
            }
        });

        final Button fingerprint = root.findViewById(R.id.btn_fingerprint);
        // Set fingerprint function
		fingerprint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    if (FingerprintUtil.supportFingerprint(getActivity())){
					FingerprintUtil.initKey();
					cipher = FingerprintUtil.initCipher();
                }
			    if (cipher != null){
					authenticateFingerprint(cipher);
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
					locked = !locked;
				} else {
                    Toast.makeText(getActivity(), "Failed!", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}


}