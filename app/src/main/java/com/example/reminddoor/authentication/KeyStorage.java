package com.example.reminddoor.authentication;

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.security.crypto.MasterKeys;

import com.yakivmospan.scytale.ErrorListener;
import com.yakivmospan.scytale.Store;

import java.security.KeyPair;

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
