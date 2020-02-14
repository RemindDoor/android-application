package com.example.reminddoor.ui.home;

import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import androidx.core.os.CancellationSignal;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.DialogFragment;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
//import androidx.fragment.app.DialogFragment;


import com.example.reminddoor.R;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class FingerprintDialogFragment extends DialogFragment {

    private FingerprintManagerCompat fingerprintManagerCompat;

    private CancellationSignal mCancellationSignal;

    private Cipher mCipher;

    private Context mActivity;

    private TextView err_msg;

    private boolean isSelfCancelled;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = getActivity();
    }

    public void setCipher(Cipher cipher){
        mCipher = cipher;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        fingerprintManagerCompat = FingerprintManagerCompat.from(mActivity);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.dialog_fingerprint, container);
        err_msg = v.findViewById(R.id.fingerprint_error_tv);
        TextView cancel = v.findViewById(R.id.cancel_tv);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                stopListening();
            }
        });

        return v;
    }

    /**
     *
     */
    @Override
    public void onResume(){
        super.onResume();
        startListening(mCipher);
    }

    /**
     *
     */
    @Override
    public void onPause(){
        super.onPause();
        stopListening();
    }

    private void startListening(Cipher cipher){
        isSelfCancelled = false;
        mCancellationSignal = new CancellationSignal();
        FingerprintManagerCompat.CryptoObject cryptoObject = new FingerprintManagerCompat.CryptoObject(cipher);
        fingerprintManagerCompat.authenticate(cryptoObject, 0, mCancellationSignal, new MyCallBack(), null);
    }

    private class MyCallBack extends FingerprintManagerCompat.AuthenticationCallback{

        // Success case
        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            if (onFingerprintSetting != null){
                onFingerprintSetting.onFingerprint(true);
            }
            dismiss();
        }

        // Failed
        @Override
        public void onAuthenticationFailed(){
            err_msg.setText("Wrong Finger, try again");
        }

        // Help message
        @Override
        public void onAuthenticationHelp(int helpId, CharSequence helpText){
            err_msg.setText(helpText);
        }

        // Error
        @Override
        public void onAuthenticationError(int helpId, CharSequence helpText){
            if(!isSelfCancelled){
                err_msg.setText(helpText);
                Toast.makeText(mActivity, "err_msgID:"+helpId, Toast.LENGTH_LONG).show();
                if(helpId == FingerprintManager.FINGERPRINT_ERROR_LOCKOUT){
                    dismiss();
                }
            }
        }
    }


    /***
     * Stop listening
     */
    private void stopListening(){
        if (mCancellationSignal != null){
            mCancellationSignal.cancel();
            mCancellationSignal = null;
            isSelfCancelled = true;
        }
    }

    private OnFingerprintSetting onFingerprintSetting;

    public void setOnFingerprintSetting(OnFingerprintSetting onFingerprintSetting) {
        this.onFingerprintSetting = onFingerprintSetting;
    }

    public interface OnFingerprintSetting{
        void onFingerprint(boolean isSucceed);
    }

    public void onDestroy(){
        super.onDestroy();
        stopListening();
    }
}
