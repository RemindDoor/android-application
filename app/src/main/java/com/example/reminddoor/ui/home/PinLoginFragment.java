package com.example.reminddoor.ui.home;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.example.reminddoor.R;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class PinLoginFragment extends DialogFragment {

    private static KeyStore ks;

    private static final String PIN_KEY_NAME = "pin_key";

    private byte[] encryption;
    private byte[] iv;

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


    private String hashString(String pin) {
        return null;
    }



    @TargetApi(23)
    byte[] encryptText(final String alias, final String text)
            throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchProviderException {
        final Cipher cipher = Cipher.getInstance(PIN_KEY_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));

        iv = cipher.getIV();

        return (encryption = cipher.doFinal(text.getBytes("UTF-8")));
    }

    @TargetApi(23)
    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchProviderException, NoSuchAlgorithmException {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        return null;
    }

}
