package com.example.reminddoor.assist;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Util {
	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = activity.getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if (view == null) {
			view = new View(activity);
		}
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	public static void hideKeyboardFrom(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public static String getCurrentTime() {
		String timeStamp = new SimpleDateFormat("HH mm ss").format(Calendar.getInstance().getTime());
		return timeStamp;
	}
	
	private static SecureRandom secureRandomGenerator = null;
	private static SecureRandom getGenerator() {
		if (secureRandomGenerator == null) {
			secureRandomGenerator = new SecureRandom();
		}
		return secureRandomGenerator;
	}
	
	public static long getRandomNum() {
		return getGenerator().nextLong();
	}
	
	public static byte[] key = new byte[16];
	public static SecretKey getKey() {
		return new SecretKeySpec(key, "AES");
	}
	
	public static byte[] encrypt(byte[] bytes, SecretKey key) {
		final Cipher cipher;
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			byte[] ivbytes = new byte[16];
			getGenerator().nextBytes(ivbytes);
			IvParameterSpec iv = new IvParameterSpec(ivbytes);
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			byte[] cipherText = cipher.doFinal(bytes);
			ByteBuffer byteBuffer = ByteBuffer.allocate(cipherText.length+ ivbytes.length);
			byteBuffer.put(ivbytes);
			byteBuffer.put(cipherText);
			return byteBuffer.array();
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
