package com.example.reminddoor.ui.home;


import android.annotation.TargetApi;
import android.content.Context;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.BufferedReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;

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

public class PinData {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private KeyStore keyStore;

    private byte[] iv;
    private byte[] settedPin;

    public PinData() {
    }

    public byte[] getSettedPin() {
        return settedPin;
    }

    public void setSettedPin(byte[] settedPin) {
        this.settedPin = settedPin;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    void writeFile(String filename, String cipher, Context context) {
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(cipher.getBytes());
            Log.d(TAG, "Saved: " + context.getFilesDir());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    String readFile(String filename, Context context){
        FileInputStream fis = null;
        String msg = "";
        try {
            fis = context.openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text);
                msg = sb.toString();
            }
            // sb: byte[]

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return msg;
    }


    @TargetApi(23)
    byte[] encryptText(final String alias, final String text)
            throws NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException,
            BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchProviderException, InvalidAlgorithmParameterException {
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));

        iv = cipher.getIV();

        return (cipher.doFinal(text.getBytes()));
    }

    @TargetApi(23)
    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE);

        keyGenerator.init(builder.build());

        return keyGenerator.generateKey();
    }


    void initKeyStore() throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    String decryptData(final String alias, final byte[] encryptedData, final byte[] encryptionIv) throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey_D(alias), spec);

        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }

    private SecretKey getSecretKey_D(final String alias) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException {
        return ((KeyStore.SecretKeyEntry) keyStore.getEntry(alias, null)).getSecretKey();
    }


}
