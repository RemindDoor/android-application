package com.example.reminddoor.bluetooth;

import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.util.Consumer;
import androidx.fragment.app.FragmentManager;

import com.example.reminddoor.MainActivity;
import com.example.reminddoor.assist.Util;
import com.example.reminddoor.ui.dashboard.DashboardFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class Protocol {
	
	private static final byte[] empty = new byte[]{};
	
	public static void unlockDoor() {
		Connectivity.sendData(Type.UNLOCK.get(), empty, null);
	}
	
	public static void removeUser(String user, final Consumer<ArrayList<String>> namesListConsumer) {
		Consumer<byte[]> byteEater = s -> {
			if (s == null) {
				return;
			}
			
			namesListConsumer.accept(split(s));
		};
		
		Connectivity.sendData(Type.REMOVE_USER.get(), Util.padRight(user, 31).getBytes(), byteEater);
	}
	
	public static void addUser(byte[] encryptedString) {
		Consumer<byte[]> byteEater = s -> {
			if (s == null) {
				Util.showToast("That QR code has timed out. Please create a new one and try again.");
				return;
			}
			
			Util.setKey(s);
			
			if (MainActivity.currentFragment == MainActivity.BottomTab.DOOR) {
				DashboardFragment.updateUsersList();
			}
		};
		
		Connectivity.sendData(encryptedString, byteEater);
	}
	
	public static void getUserList(final Consumer<ArrayList<String>> namesListConsumer) {
		Consumer<byte[]> byteEater = s -> {
			if (s == null) {
				return;
			}
			
			namesListConsumer.accept(split(s));
		};
		Connectivity.sendData(Type.GET_ALL_USERS.get(), empty, byteEater);
	}
	
	/**
	 * Unlocks the door, links the phone up with the Arduino, disables the old link, all sorts of things.
	 */
	public static void guestUnlockSignInThingy() {
		Consumer<byte[]> byteEater = s -> {
			Util.setKey(s);
			Protocol.unlockDoor();
		};
		
		Connectivity.sendData(Type.SWAP_OUT_GUEST.get(), empty, byteEater);
	}
	
	public static void ackReminder() {
		Connectivity.sendData(Type.ACK_REMINDER.get(), empty, null);
	}
	
	public static void createGuestAccount(String name, long startTime, long endTime) throws IOException {
		Consumer<byte[]> byteEater = s -> {
			String toDecode = Base64.getUrlEncoder().encodeToString(s);
			
			final TextView text = new TextView(MainActivity.mainActivity);
			text.setTextIsSelectable(true);
			text.setFocusable(true);
			text.setLongClickable(true);
			text.setEnabled(true);
			text.setText("www.reminddoor.com/guest/" + toDecode);
			Util.popupBox(MainActivity.mainActivity, "URL", () -> {}, text);
		};
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(Util.padRight(name, 31).getBytes());
		outputStream.write(Util.longToBytes(startTime));
		outputStream.write(Util.longToBytes(endTime));
		Connectivity.sendData(Type.NEW_GUEST.get(), outputStream.toByteArray(), byteEater);
	}
	
	private static ArrayList<String> split(byte[] s) {
		String[] splitNames = new String(s).split("\\|");
		
		ArrayList<String> names = new ArrayList<>();
		for (String name : splitNames) {
			if (name.length() > 0) {
				names.add(name);
			}
		}
		return names;
	}
	
	
	/*
	 * Case list:
	 * 0: Unlocking door request
	 * 1: ADMIN - Create temporary guest account (Logs it for replacement and adds timestamps)
	 * 2: ADMIN - Generate new user request (Generates new key and adds user name)
	 * 3: ADMIN - Remove user request (You provide a name).
	 *      | --- 32 bytes of name --- |
	 * 4: ADMIN - Get all authenticated users.
	 *      | --- nothing --- |
	 * 5: My name change request.
	 *      | --- 32 bytes of new name --- |
	 * 6: ADMIN - Others change name request.
	 *      | --- 32 bytes of old name --- | | --- 32 bytes of new name --- |
	 * 7: Swaps out guest key for new random key and gives to guest.
	 */
	
	public enum Type {
		UNLOCK,
		NEW_GUEST,
		NEW_USER,
		REMOVE_USER,
		GET_ALL_USERS,
		CHANGE_MY_NAME,
		CHANGE_OTHER_NAME,
		SWAP_OUT_GUEST,
		ACK_REMINDER;
		
		public byte get() {
			return (byte) this.ordinal();
		}
	}
}
