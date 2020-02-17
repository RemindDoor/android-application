package com.example.reminddoor.ui.home;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.example.reminddoor.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class PinLoginFragment extends DialogFragment {
    private String filename_iv = "my_iv.txt";
    private String filename_pin = "my_pin.txt";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String PIN_KEY_NAME = "pin_key";

    private PinData myPin = new PinData();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_pin, container);
        PinLockView mPinLockView = v.findViewById(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        return v;
    }

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);

            // read iv and pin from file

            String s = myPin.readFile(filename_iv, getActivity());
            Log.d(TAG, "iv:" + s);

            byte[] newIv = Base64.decode(s, Base64.DEFAULT);

            String string_pin = myPin.readFile(filename_pin, getActivity());
            byte[] newcipher = Base64.decode(string_pin, Base64.DEFAULT);


            String d = "";
            try {
                myPin.initKeyStore();
                d = myPin.decryptData(PIN_KEY_NAME, newcipher, newIv);
                Log.d(TAG, "Decryption complete: " + d);
            } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | NoSuchPaddingException | UnrecoverableEntryException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }

            if (d.equals(pin)){
                pinSetting.checkPin(true);
            }


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

    private PinSetting pinSetting;

    public void setPinSetting(PinSetting pinSetting) {
        this.pinSetting = pinSetting;
    }

    public interface PinSetting{
        void checkPin(boolean isSucceed);
    }


}
