package com.example.reminddoor.ui.home;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.example.reminddoor.R;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;


import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class SettingPinFragment extends DialogFragment{

    private String filename_iv = "my_iv.txt";
    private String filename_pin = "my_pin.txt";
    private static final String PIN_KEY_NAME = "pin_key";
    private PinData myPin = new PinData();
    private byte[] iv;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_setting_pin, container);
        PinLockView mPinLockView = v.findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        return v;
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            // write iv and pin into files
            byte[] cipherText;
            String cipher = "";
            String iv_string;
            try {
                cipherText = myPin.encryptText(PIN_KEY_NAME, pin);
                cipher = Base64.encodeToString(cipherText, Base64.DEFAULT);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnsupportedEncodingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }

            iv = myPin.getIv();
            iv_string = Base64.encodeToString(iv, Base64.DEFAULT);
            // write
            myPin.writeFile(filename_iv, iv_string, getActivity());
            myPin.writeFile(filename_pin, cipher, getActivity());
            Log.d(TAG, "iv: " + iv_string);



            dismiss();



        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };
}
