package com.example.reminddoor.assist;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.ui.home.DisabledFragment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
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
	
	public static void setKey(byte[] key) {
		if (key.length != 16) {
			throw new RuntimeException("The key was invalid.");
		}
		
		if (DisabledFragment.dismiss != null) {
			DisabledFragment.dismiss.run();
		}
		save(MainActivity.ctx.getDir("data", 0), key);
	}
	
	public static SecretKey getKey() {
		byte[] key = load(MainActivity.ctx.getDir("data", 0));
		
		if (key == null) {
			return null;
		}
		
		return new SecretKeySpec(key, "AES");
	}
	
	public static void showToast(String msg) {
		MainActivity.mainActivity.runOnUiThread(() -> Toast.makeText(MainActivity.ctx, msg, Toast.LENGTH_LONG).show());
		
	}
	
	private static void save(File f, byte[] data) {
		File file = new File(f, "key");
		try {
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));
			outputStream.write(data);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] load(File f) {
		File file = new File(f, "key");
		if (!file.exists()) return null;
		int size = (int) file.length();
		byte[] bytes = new byte[size];
		try {
			BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
			buf.read(bytes, 0, size);
			buf.close();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] longToBytes(long loong) {
		return new byte[] {
				(byte) loong,
				(byte) (loong >> 8),
				(byte) (loong >> 16),
				(byte) (loong >> 24),
				(byte) (loong >> 32),
				(byte) (loong >> 40),
				(byte) (loong >> 48),
				(byte) (loong >> 56)};
	}
	
	public static void popupBox(Context context, String title, Runnable runnable, View view) {
		MainActivity.mainActivity.runOnUiThread(() -> {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			
			builder.setPositiveButton("OK", (dialog, which) -> {
				runnable.run();
			});
			builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
			
			builder.setTitle(title);
			builder.setView(view);
			builder.show();
		});
	}

	public static void popupBox(Context context, String title, Runnable runnable, View view, String code) {
			MainActivity.mainActivity.runOnUiThread(() -> {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);

				builder.setPositiveButton("Copy to Clipboard", (dialog, which) -> {
					android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
					android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", code);
					clipboard.setPrimaryClip(clip);
					showToast("Copied to clipboard!");
					runnable.run();
				});
				builder.setNegativeButton("Email", (dialog, which) ->
				{
					MainActivity.mainActivity.sendGuestEmail(code);
					dialog.cancel(); });

				builder.setTitle(title);
				builder.setView(view);
				builder.show();
			});
	}
	
	public static String padRight(String s, int n) {
		return String.format("%-" + n + "s", s);
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
