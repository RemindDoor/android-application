package com.example.reminddoor.authentication;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.security.crypto.MasterKeys;

import com.yakivmospan.scytale.ErrorListener;
import com.yakivmospan.scytale.Store;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class KeyStorage {
	public static byte[] getSecretKey(Context applicationContext){
		Store store = new Store(applicationContext);
		store.setErrorListener(new ErrorListener() {
			@Override
			public void onError(Exception e) {
				System.out.println(e.getMessage());
			}
		});
		KeyPair key = store.generateAsymmetricKey("RemindDoor", "Hello".toCharArray());
		if (key == null) {
			key = store.getAsymmetricKey("RemindDoor", "Hello".toCharArray());
		}
		return key.getPrivate().getEncoded();
	}
}
