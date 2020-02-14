package com.example.reminddoor.assist;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec.Builder;
import android.security.keystore.KeyProperties;

import android.widget.Toast;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class FingerprintUtil {

    private static KeyStore ks;

    private static final String DEFAULT_KEY_NAME = "default_key";


    public static boolean supportFingerprint(Context context){
        if (Build.VERSION.SDK_INT < 23){
            Toast.makeText(context, "Android version is too low", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            KeyguardManager keyguardManager = context.getSystemService(KeyguardManager.class);
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(context);
            if (!fingerprintManager.isHardwareDetected()){
                Toast.makeText(context, "Your phone doesn't support FingerPrint", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!keyguardManager.isKeyguardSecure()){
                Toast.makeText(context, "Your doesn't set screen lock", Toast.LENGTH_SHORT).show();
                return false;
            } else if (!fingerprintManager.hasEnrolledFingerprints()){
                Toast.makeText(context, "Your should store at least one fingerprint", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    @TargetApi(23)
    public static void initKey(){
        try {
            ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);
            KeyGenerator kG = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");
            Builder builder = new Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            kG.init(builder.build());
            kG.generateKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @TargetApi(23)
    public static Cipher initCipher() {
        Cipher cipher;
        try{
            SecretKey key = (SecretKey) ks.getKey(DEFAULT_KEY_NAME, null);
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
        return cipher;
    }

}
